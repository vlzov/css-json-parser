package com.vlzov;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.vlzov.Entity.Employee;

public class Main {
    public static void main(String[] args) {

        /*
            Block which parse CSV ---> JSON
        */
        
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};

        String csvFileName = "/Users/vladimir/Documents/csv-json-parser/src/main/resources/data.csv";

        List<Employee> csvList = parseCSV(columnMapping, csvFileName);

        String json1 = listToJson(csvList);
        
        writeString(json1, "/Users/vladimir/Documents/csv-json-parser/src/main/resources/data1.json");

        
        /*
            Block which parse XML ---> JSON
        */

        String xmlFileName = "/Users/vladimir/Documents/csv-json-parser/src/main/resources/data.xml";
        
        List<Employee> xmlList = parseXML(xmlFileName);

        String json2 = listToJson(xmlList);

        writeString(json2, "/Users/vladimir/Documents/csv-json-parser/src/main/resources/data2.json");
        
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
            
            System.out.println("Ошибка парсинга CSV файла: " + e.getMessage());
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

    public static List<Employee> parseXML(String fileName) {

        List<Employee> employees = new ArrayList<>();

        try {
            
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(fileName);

            Element root = document.getDocumentElement();

            NodeList nodeList = root.getElementsByTagName("employee");

            for(int i = 0; i < nodeList.getLength(); i++) {

                Element employeeElement = (Element) nodeList.item(i);

                long id = Long.parseLong(getTagValue("id", employeeElement));
                String firstName = getTagValue("firstName", employeeElement);
                String lastName = getTagValue("lastName", employeeElement);
                String country = getTagValue("country", employeeElement);
                int age = Integer.parseInt(getTagValue("age", employeeElement));      
                
                Employee employee = new Employee(id, firstName, lastName, country, age);
                employees.add(employee);

            }

        } catch (Exception e) {
            System.out.println("Ошибка парсинга XML файла: " + e.getMessage());
            e.printStackTrace();
        }

        return employees;

    }

    private static String getTagValue(String tagName, Element element) {

        NodeList nodeList = element.getElementsByTagName(tagName);

        if(nodeList.getLength() > 0) {

            Node node = nodeList.item(0);

            if(node != null) {

                return node.getTextContent();

            }

        }

        return "";

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
