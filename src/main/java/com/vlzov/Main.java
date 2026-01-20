package com.vlzov;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};

        String fileName = "/Users/vladimir/Documents/csv-json-parser/src/main/resources/data.csv";

        List<Employee> list = parseCSV(columnMapping, fileName);

        String json = listToJson(list);
        
        writeString(json, "/Users/vladimir/Documents/csv-json-parser/src/main/resources/data.json");
        
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {

        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            
            ColumnPositionMappingStrategy<Employee> strategy = 
                new ColumnPositionMappingStrategy<>();
            
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            
            CsvToBean<Employee> csvToBean = new CsvToBeanBuilder<Employee>(reader)
                .withMappingStrategy(strategy)
                .build();
            
            return csvToBean.parse();

        } catch (IOException e) {
            
            System.out.println("Ошибка чтения CSV файла: " + e.getMessage());
            return List.of();
        }

    }

    public static String listToJson(List<Employee> list) {

        Gson gson = new GsonBuilder()
                            .setPrettyPrinting()
                            .create();

        Type listType = new TypeToken<List<Employee>>() {}.getType();

        return gson.toJson(list, listType);

    }

    public static void writeString(String json, String fileName) {

        try (FileWriter fileWriter = new FileWriter(fileName)){
            
            fileWriter.write(json);
            fileWriter.flush();

        } catch (IOException e) {
            System.out.println("Ошибка записи JSON в файл: " + e);
        }

    }
}
