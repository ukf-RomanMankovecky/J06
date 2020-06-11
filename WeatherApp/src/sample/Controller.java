package sample;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;

public class Controller {

    private String APIKEY = "84fdec0f67c06b68eb7ccb6e4582d0ac";
    @FXML private TextField CommandPane;
    SQLiteJDBC sqlConnector;

    @FXML private Label cityAtStart;
    @FXML private Label firstDate;
    @FXML private Label secondDate;
    @FXML private Label thirdDate;

    @FXML private Label secondCity;
    @FXML private Label secondCityDate1;
    @FXML private Label secondCityDate2;
    @FXML private Label secondCityDate3;

    private File file;
    static String firstColor = "";
    static String secondColor = "";
    static String thirdColor = "";
    static String first = "";
    static String second = "";
    static String third = "";


    @FXML
    protected void initialize() throws SQLException {
        sqlConnector = new SQLiteJDBC();
        boolean created = false;
        this.file = new File("LastSearched.txt");

        try {
            if (file.createNewFile())
            {
                created = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!created) {
            initializeFirstCity();
        }
    }

    private void initializeFirstCity() {
        String line = "";
        try {
            FileReader fr = new FileReader(this.file);
            BufferedReader br = new BufferedReader(fr);

            line = br.readLine();

        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
        parseFirstCity(line);
    }

    private void parseFirstCity(String line) {
        boolean prevTemp = true;
        if (line != null) {
            for (int i = 0; i < line.length(); i++) {
                if ((line.charAt(i) == '@') && (line.charAt(i + 1) == '@')) {
                    prevTemp = false;
                }
            }
            if (prevTemp) {
                String[] arr = line.split("@");
                downloadWeatherData(sqlConnector.getIdFromName(arr[0]));
                this.cityAtStart.setText(arr[0]);
                this.firstDate.setText(first);
                this.secondDate.setText(second);
                this.thirdDate.setText(third);
            } else {
                downloadWeatherData(sqlConnector.getIdFromName("Nitra"));
                this.cityAtStart.setText("Nitra");
                this.firstDate.setText(first);
                this.secondDate.setText(second);
                this.thirdDate.setText(third);
            }
        }
        else {
            downloadWeatherData(sqlConnector.getIdFromName("Nitra"));
            this.cityAtStart.setText("Nitra");
            this.firstDate.setText(first);
            this.secondDate.setText(second);
            this.thirdDate.setText(third);
        }
    }

    private void downloadWeatherData(long id) {
        HttpClient client = HttpClient.newHttpClient();
        String idUrl = "https://api.openweathermap.org/data/2.5/forecast?id=" + id + "&units=metric&appid=" + APIKEY;

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(idUrl)).build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(Controller::parseCurrentWeather)
                .join();
    }

    private static String parseCurrentWeather(String response) {
        first = "";
        second = "";
        third = "";
        JSONArray resultDates = new JSONArray("[" + response + "]" );

        JSONArray list = null;
        try {
            list = resultDates.getJSONObject(0).getJSONArray("list");
        }catch (Exception e) {
            System.out.println("Invalid json");
            return null;
        }

        //den 1
        JSONObject obj1 = list.getJSONObject(0);
        JSONArray weather1 = obj1.getJSONArray("weather");
        String desctiprion1 = weather1.getJSONObject(0).getString("description");
        Object data1 = obj1.get("dt_txt");
        JSONObject main1 = obj1.getJSONObject("main");
        double temperature1 = Double.parseDouble(main1.get("temp").toString());

        //den 2
        JSONObject obj2 = list.getJSONObject(8);
        JSONArray weather2 = obj2.getJSONArray("weather");
        String description2 = weather2.getJSONObject(0).getString("description");
        Object data2 = obj2.get("dt_txt");
        JSONObject main2 = obj2.getJSONObject("main");
        double temperature2 = Double.parseDouble(main2.get("temp").toString());

        //den 3
        JSONObject obj3 = list.getJSONObject(16);
        JSONArray weather3 = obj3.getJSONArray("weather");
        String description3 = weather3.getJSONObject(0).getString("description");
        Object data3 = obj3.get("dt_txt");
        JSONObject main3 = obj3.getJSONObject("main");
        double temperature3 = Double.parseDouble(main3.get("temp").toString());

        //teplota den 1
        if (temperature1 < 5) {
            firstColor = "blue";
        }else if ((temperature1 >= 5) && (temperature1 < 15)) {
            firstColor = "light blue";
        }else if ((temperature1 >= 16) && (temperature1 < 20)) {
            firstColor = "grey";
        }else if ((temperature1 >= 21) && (temperature1 < 25)){
            firstColor = "orange";
        }else {
            firstColor = "red";
        }

        String date1 = data1.toString().substring(0, 10);
        first = date1 + ", teplota: " + temperature1 + "°C, " + desctiprion1 ;

        // teplota den 2
        if (temperature2 < 5) {
            secondColor = "blue";
        }else if ((temperature2 >= 5) && (temperature2 < 15)) {
            secondColor = "light blue";
        }else if ((temperature2 >= 16) && (temperature2 < 20)) {
            secondColor = "grey";
        }else if ((temperature2 >= 21) && (temperature2 < 25)){
            secondColor = "orange";
        }else {
            secondColor = "red";
        }

        String date2 = data2.toString().substring(0, 10);
        second = date2 + ", teplota: " + temperature2 + "°C, " + description2 ;

        // teplota den 3
        if (temperature3 < 5) {
            thirdColor = "blue";
        }else if ((temperature3 >= 5) && (temperature3 < 15)) {
            thirdColor = "light blue";
        }else if ((temperature3 >= 16) && (temperature3 < 20)) {
            thirdColor = "grey";
        }else if ((temperature3 >= 21) && (temperature3 < 25)){
            thirdColor = "orange";
        }else {
            thirdColor = "red";
        }

        String date3 = data3.toString().substring(0, 10);
        third = date3 + ", teplota: " + temperature3 + "°C, " + description3 ;
        return null;
    }

    public void find(ActionEvent event) {
        String City = this.CommandPane.getText();
        try {
            downloadWeatherData(sqlConnector.getIdFromName(City));
            String content = City +
                    "@" + first + "@" + firstColor +
                    "@" + second + "@" + secondColor +
                    "@" + third + "@" + thirdColor;

            FileWriter fw = new FileWriter(this.file, false);
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write(content);
            bw.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        this.secondCity.setText(City);
        this.secondCityDate1.setTextFill(Paint.valueOf(firstColor));
        this.secondCityDate2.setTextFill(Paint.valueOf(secondColor));
        this.secondCityDate3.setTextFill(Paint.valueOf(thirdColor));
        this.secondCityDate1.setText(first);
        this.secondCityDate2.setText(second);
        this.secondCityDate3.setText(third);

        this.CommandPane.setText("");
    }
}