package com.company.api;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class Api {

    private static final String basicUrl = "http://localhost:8000";

    private final String user_key;
    private final int problem_id;
    private final int number_of_elevators;
    private String token;

    public Api(String user_key, int problem_id, int number_of_elevators) {
        this.user_key = user_key;
        this.problem_id = problem_id;
        this.number_of_elevators = number_of_elevators;
    }

    public String getUser_key() {
        return user_key;
    }

    public int getProblem_id() {
        return problem_id;
    }

    public int getNumber_of_elevators() {
        return number_of_elevators;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String start(){
        String sendUrl = basicUrl + "/start/" + getUser_key() + "/" + getProblem_id() + "/" + getNumber_of_elevators();

        String inputLine = null;
        StringBuffer outResult = new StringBuffer();

        try{
            URL url = new URL(sendUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");

            int responseCode = conn.getResponseCode();
            switch(responseCode){
                case 200:
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    while((inputLine = in.readLine()) != null){
                        outResult.append(inputLine);
                    }
                    break;

                default:
                    System.out.println("ERROR");
                    throw new Exception();
            }

            conn.disconnect();

        }catch(Exception e){
            e.printStackTrace();
        }

        return outResult.toString();
    }

    public String on_calls(){
        String sendUrl = basicUrl + "/oncalls";

        String inputLine = null;
        StringBuffer outResult = new StringBuffer();

        try{
            URL url = new URL(sendUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("X-Auth-Token", this.getToken());

            int responseCode = conn.getResponseCode();
            switch(responseCode){
                case 200:
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    while((inputLine = in.readLine()) != null){
                        outResult.append(inputLine);
                    }
                    break;

                default:
                    System.out.println("ERROR");
                    throw new Exception();
            }

            conn.disconnect();

        }catch(Exception e){
            e.printStackTrace();
        }

        return outResult.toString();
    }

    public String action(String actionJson){
        String sendUrl = basicUrl + "/action";

        String inputLine = null;
        StringBuffer outResult = new StringBuffer();

        try{
            URL url = new URL(sendUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("X-Auth-Token", this.getToken());
            conn.setRequestProperty("Content-Type", "application/json");

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            bw.write(actionJson);
            bw.flush();
            bw.close();

            int responseCode = conn.getResponseCode();
            switch(responseCode){
                case 400:
                    System.out.println("Bad Request");
                    throw new Exception();

                case 401:
                    System.out.println("Unauthorized");
                    break;

                case 500:
                    System.out.println("Internal Server Error");
                    break;

                case 200:
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    while((inputLine = in.readLine()) != null){
                        outResult.append(inputLine);
                    }
                    break;
            }

            conn.disconnect();

        }catch(Exception e){
            e.printStackTrace();
        }

        return outResult.toString();
    }
}
