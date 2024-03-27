package Weather.Classes;

import java.applet.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.net.URI;
import com.owlike.genson.Genson;


class Day
{
    String datetime;
    String description;
    double temp;
    double humidity;
    double windspeed;
    double winddir;
    String icon;
    String conditions;
    Hour[] hours;
}

class Hour
{
    String datetime;
    double temp;
    double humidity;
    double windspeed;
}

class Current
{
    String datetime;
    double temp;
    double humidity;
    double windspeed;
    double windgust;
    double winddir;
    String icon;
    String conditions;

}


class WeatherJson
{
    double latitude;
    double longitude;
    Day[] days;
    Current currentConditions;
    String description;
}

class TodayButton implements ActionListener
{
    static Genson genson = new Genson();
    static final Toolkit toolkit = Toolkit.getDefaultToolkit();
    private static CheckboxGroup cb;
    private static Label[] result;
    private Weather wapp;
    static String location;
    static CloseableHttpClient httpClient = HttpClients.createDefault();
    static HttpGet getRequest;
    static HttpResponse response;
    static Image weathericon;
    static WeatherJson w;

    TodayButton(){}
    TodayButton(CheckboxGroup a, Label[] b, Weather c)
    {
        cb = a;
        result = b;
        wapp = c;
    }

    static String Selector(String s) throws Exception
    {
        String responseBody;
        switch(s)
        {
            case "Pune":
                getRequest = new HttpGet(new URI("https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/Pune?unitGroup=metric&key=SEJ9JLY4WBTB84NP4Z5E5BG8C&contentType=json"));
                break;
            case "Mumbai":
                getRequest = new HttpGet(new URI("https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/Mumbai?unitGroup=metric&key=SEJ9JLY4WBTB84NP4Z5E5BG8C&contentType=json"));
                break;
            case "Delhi":
                getRequest = new HttpGet(new URI("https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/Delhi?unitGroup=metric&key=SEJ9JLY4WBTB84NP4Z5E5BG8C&contentType=json"));
                break;
            case "Gujarat":
                getRequest = new HttpGet(new URI("https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/Gujarat?unitGroup=metric&key=SEJ9JLY4WBTB84NP4Z5E5BG8C&contentType=json"));
                break;
            case "Chennai":
                getRequest = new HttpGet(new URI("https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/Chennai?unitGroup=metric&key=SEJ9JLY4WBTB84NP4Z5E5BG8C&contentType=json"));
                break;
            default:
                break;
        }
                    response = httpClient.execute(getRequest);
                    responseBody = EntityUtils.toString(response.getEntity());
                    return responseBody;
    }

    static String Responser()
    {
        location = cb.getSelectedCheckbox().getLabel();
        String responseBody="";

        try {
            responseBody = Selector(location);
        }
        catch (Exception e1)
        {
            System.out.println(e1.toString());
        }
        return responseBody;
    }

    static Image IconFetcher(String icon_name)
    {
        return toolkit.getImage("icons\\" + icon_name + ".png");
    }

    static String MonthSelector(String num)
    {
        int month = Integer.parseInt(num);

        switch (month)
        {
            case 1:
                return "January";
            case 2:
                return "February";
            case 3:
                return "March";
            case 4:
                return "April";
            case 5:
                return "May";
            case 6:
                return "June";
            case 7:
                return "July";
            case 8:
                return "August";
            case 9:
                return "September";
            case 10:
                return "October";
            case 11:
                return "November";
            case 12:
                return "December";
            default:
                return null;
        }
    }
    public void actionPerformed(ActionEvent e)
    {

        String responseBody=Responser();

        if(responseBody==null)
        {
            result[0].setText("Couldn't Reach The API please try again (make sure to turn on internet)");
        }
        else {
            w = genson.deserialize(responseBody, WeatherJson.class);
            result[1].setText(w.currentConditions.temp + "°C".substring(1));
            result[2].setText("Humidity:  " + w.currentConditions.humidity + " %");
            result[3].setText("Windspeed:  " + w.currentConditions.windspeed + " kph");
            result[4].setText("Wind Direction:  " + w.currentConditions.winddir);
            result[5].setText(w.description);
            result[6].setText(w.days[0].datetime.substring(8,10) + " " + MonthSelector(w.days[0].datetime.substring(5,7))+ " " + w.days[0].datetime.substring(0,4));
            result[7].setText(w.currentConditions.conditions);
            TodayButton.weathericon = IconFetcher(w.currentConditions.icon);

            wapp.refresh();

        }
    }
}

class TomorrowButton extends TodayButton implements ActionListener
{
    private static CheckboxGroup cb;
    private static Label[] result;
    private Weather wapp;
    TomorrowButton(CheckboxGroup a, Label[] b, Weather c)
    {

        cb = a; result = b; wapp = c;
    }

    public void actionPerformed(ActionEvent event)
    {
        String responseBody=Responser();

        if(responseBody==null)
        {
            result[0].setText("Couldn't Reach The API please try again (make sure to turn on internet)");
        }
        else {
            w = genson.deserialize(responseBody, WeatherJson.class);
            result[1].setText(tempAtHour(w.days[1].hours) + "°C".substring(1));
            result[2].setText("Humidity:  " + w.days[1].humidity + " %");
            result[3].setText("Windspeed:  " + w.days[1].windspeed + " kph");
            result[4].setText("Wind Direction:  " + w.days[1].winddir);
            result[5].setText(w.description);
            result[6].setText(w.days[1].datetime.substring(8,10) + " " + MonthSelector(w.days[1].datetime.substring(5,7))+ " " + w.days[1].datetime.substring(0,4));
            result[7].setText(w.days[1].conditions);
            TodayButton.weathericon = IconFetcher(w.days[1].icon);

            wapp.refresh();

        }

    }
    static double tempAtHour(Hour[] h)
    {
        int i;
        String currenthour = w.currentConditions.datetime.substring(0,2);
        for(i=0;!h[i].datetime.substring(0,2).equals(currenthour) && i<=23;i++)
        {
        }
        return h[i].temp;
    }
}

class TodayHourReport extends TodayButton implements ActionListener
{
    private static CheckboxGroup cb;
    private static Label[] result;
    private Weather wapp;
    TodayHourReport(CheckboxGroup a, Label[] b, Weather c)
    {
        cb = a;
        result = b;
        wapp = c;
    }

    public void actionPerformed(ActionEvent event)
    {
        String responseBody = Responser();

        if(responseBody == null)
        {
            result[0].setText("Couldn't Reach The API please try again (make sure to turn on internet)");
        }
        else
        {
            w = genson.deserialize(responseBody, WeatherJson.class);
            result[24].setText("Today's Hourly Report");
            for(int i = 0; i < result.length-1; i++)
            {
            result[i].setText(w.days[0].hours[i].datetime + " --> " + w.days[0].hours[i].temp + "°C | ".substring(1) + w.days[0].hours[i].humidity + "% | " + w.days[0].hours[i].windspeed + " kph");
            }
            wapp.refresh();
        }
    }
}

class Forecast14Days extends TodayButton implements ActionListener
{
    private static CheckboxGroup cb;
    private static Label[] result;
    private Weather wapp;

    Forecast14Days(CheckboxGroup a, Label[] b, Weather c)
    {
        cb = a;
        result = b;
        wapp = c;
    }

    public void actionPerformed(ActionEvent event)
    {
        String responseBody = Responser();

        if(responseBody == null)
        {
            result[0].setText("Couldn't Reach The API please try again (make sure to turn on internet)");
        }
        else
        {
            w = genson.deserialize(responseBody, WeatherJson.class);
            result[24].setText("Next 14 Day's Forecast");
            for(int i = 0,j=1; i < result.length-1; i+=2,j++)
            {
                result[i].setText(w.days[j].datetime + " --> " + w.days[j].temp + "°C | ".substring(1) + w.days[j].humidity + " % | " + w.days[j].windspeed + " kph");
                result[i+1].setText("Forecast: " + w.days[j].description);
            }
            wapp.refresh();
        }
    }
}

public class Weather extends Applet
{
    private Button today, tom ,hourly,forecast14;
    private CheckboxGroup location;
    private static final Toolkit toolkit = Toolkit.getDefaultToolkit();
    Image icon = toolkit.getImage("logo.jpg");
    Font headfont = new Font("Times New Roman",Font.BOLD,30);
    Font head2font = new Font("Times New Roman",Font.PLAIN,19);
    Font head3font = new Font("Times New Roman",Font.PLAIN,16);
    Font normalfont = new Font("Times New Roman" , Font.PLAIN ,14);
    public Label[] l,l2;
    private final int x = 30;

    public void refresh()
    {
        repaint();
    }

    public void init()
    {

        setLayout(null);

        Label locLabel = new Label("Location");
        locLabel.setBounds(x,80,150,30);
        locLabel.setFont(head2font);
        add(locLabel);

        location = new CheckboxGroup();
        Checkbox pune = new Checkbox("Pune",location,true);
        Checkbox mumbai = new Checkbox("Mumbai", location, false);
        Checkbox delhi = new Checkbox("Delhi", location, false);
        Checkbox gujarat = new Checkbox("Gujarat", location, false);
        Checkbox chennai = new Checkbox("Chennai", location, false);
        pune.setBounds(x, 120, 100, 20);
        mumbai.setBounds(x, 140, 100, 20);
        delhi.setBounds(x, 160, 100, 20);
        gujarat.setBounds(x, 180, 100, 20);
        chennai.setBounds(x, 200, 100, 20);
        add(pune);
        add(mumbai);
        add(delhi);
        add(gujarat);
        add(chennai);

        l = new Label[8];
        l2 = new Label[25];

//        l[0] = new Label("");
//        l[0].setBounds(x,300,500,20);
//        l[0].setFont(head2font);
//        add(l[0]);

        l[1] = new Label("");
        l[1].setBounds(x,300,500,20);
        l[1].setFont(head2font);
        add(l[1]);

        l[2] = new Label("");
        l[2].setBounds(x,330,135,20);
        l[2].setFont(head3font);
        add(l[2]);

        l[3] = new Label("");
        l[3].setBounds(x+145,330,160,20);
        l[3].setFont(head3font);
        add(l[3]);

        l[4] = new Label("");
        l[4].setBounds(x+320,330,160,20);
        l[4].setFont(head3font);
        add(l[4]);

        l[5] = new Label("");
        l[5].setBounds(x,360,500,20);
        l[5].setFont(normalfont);
        add(l[5]);

        l[6] = new Label("");
        l[6].setBounds(x,390,500,20);
        l[6].setFont(normalfont);
        add(l[6]);

        l[7] = new Label("");
        l[7].setBounds(x+600,410,500,20);
        l[7].setFont(normalfont);
        add(l[7]);


        today = new Button("Today's Weather");
        today.setBackground(new Color(90, 171, 173));
        today.setBounds(x, 250 , 130, 30);
        today.addActionListener(new TodayButton(location,l,this));
        add(today);

        tom = new Button("Tomorrow's Weather");
        tom.setBackground(new Color(90, 171, 173));
        tom.setBounds(x+150, 250 , 130, 30);
        tom.addActionListener(new TomorrowButton(location,l,this));
        add(tom);

        hourly = new Button("Today's Hourly Report");
        hourly.setBackground(new Color(90, 171, 173));
        hourly.setBounds(x, 430 , 130, 30);
        hourly.addActionListener(new TodayHourReport(location,l2,this));
        add(hourly);

        forecast14 = new Button("14 Day Forecast");
        forecast14.setBackground(new Color(90, 171, 173));
        forecast14.setBounds(x+150, 430 , 130, 30);
        forecast14.addActionListener(new Forecast14Days(location,l2,this));
        add(forecast14);

        l2[24] = new Label("");
        l2[24].setFont(head2font);
        l2[24].setBounds(x, 470 , 300, 30);
        add(l2[24]);

        int incmargin = 30;
        for(int i=0;i<=11;i++)
        {
            l2[i] = new Label("");
            l2[i].setFont(normalfont);
            l2[i].setBounds(x, 480+incmargin , 300, 30);
            add(l2[i]);
            incmargin += 30;
        }
        incmargin=30;
        for(int i=12;i<=23;i++)
        {
            l2[i] = new Label("");
            l2[i].setFont(normalfont);
            l2[i].setBounds(x+400, 480+incmargin , 300, 30);
            add(l2[i]);
            incmargin += 30;
        }


        Frame f1 = (Frame) getParent().getParent();
        f1.setTitle("Weather App");
        f1.setIconImage(icon);
        f1.setVisible(true);
        f1.setBounds(0,0,1200,1000);
        f1.setBackground(Color.red);
    }

    public void paint(Graphics g)
    {

        int centerX = (int) (getWidth() / 2) - 125;
        g.setFont(headfont);
        ((java.awt.Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(250, 200, 100));
        g.fillRect(0,0,getWidth(),60);
        g.setColor(Color.black);
        g.drawString("Weather Forecast", centerX , 40);
        g.drawImage(TodayButton.weathericon,x+600,300,this);

    }

}

/*
<html>

<body>
<applet code = "Weather.class" archive="Weather/Classes/" width="1200" height="1000"> Weather App </applet>
</body>

</html>

*/
