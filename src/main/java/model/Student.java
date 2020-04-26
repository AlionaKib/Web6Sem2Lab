package model;


import model.exception.WrongAveragePoint;

import javax.naming.StringRefAddr;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.*;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@XmlType
@XmlRootElement(name = "student")
public class Student implements Serializable{
    private String name;
    private int idCardNumber;
    private String graduateSubscript;
    private double averagePoint;

    public Student() {
    }

    public Student(String name, int idCardNumber, String graduateSubscript, double averagePoint) throws WrongAveragePoint, IOException {
        if(wordsCount(name)==3)
            this.name = name;
        else
            throw new IOException("Wrong name"); //some comment

        this.idCardNumber = idCardNumber;

        this.graduateSubscript = graduateSubscript;

        if(averagePoint>=0 && averagePoint<=5)
            this.averagePoint = averagePoint;
        else
            throw new WrongAveragePoint("WrongAveragePoint");
    }

    private static int wordsCount(String str) {
        StringTokenizer ins = new StringTokenizer(str);
        int cnt = 0;
        while (ins.hasMoreTokens()){
            ins.nextToken();
            ++cnt;
        }
        return cnt;
    }

    private static boolean isDigits(String str) {
        return str.matches("[\\d]+");
    }

    @XmlElement(name = "student-name")
    public String getName() {
        return name;
    }

    public void setName(String name) throws IOException {
        if(!(isDigits(name)))
            this.name = name;
        else
            throw new IOException("Name contains digits");
    }

    @XmlElement(name = "student-id")
    public int getIdCardNumber() {
        return idCardNumber;
    }

    public void setIdCardNumber(int idCardNumber) {
        this.idCardNumber = idCardNumber;
    }

    @XmlTransient
    public String getGraduateSubscript() {
        return graduateSubscript;
    }

    public void setGraduateSubscript(String graduateSubscript) {
        this.graduateSubscript = graduateSubscript;
    }

    @XmlElement(name = "student-averagePoint")
    public double getAveragePoint() {
        return averagePoint;
    }

    public void setAveragePoint(double averagePoint) throws WrongAveragePoint {
        if(averagePoint>0 && averagePoint<=5)
            this.averagePoint = averagePoint;
        else
            throw new WrongAveragePoint("WrongAveragePoint");
    }

    public void write (Writer out){
        PrintWriter p = new PrintWriter(out);
        p.println(this.getName());
        p.println(this.getAveragePoint());
        p.println(this.getGraduateSubscript());
        p.println(this.getIdCardNumber());
        //System.out.println("Writing done");
    }

    public void read (StreamTokenizer in){
        String name = "";
        int idCardNumber;
        String graduateSubscript;
        double averagePoint;
        try {
            for (int i=0; i<3; ++i) {
                name = name + in.sval + " ";
                in.nextToken();
            }
            averagePoint = in.nval;
            in.nextToken();
            graduateSubscript = in.sval;
            in.nextToken();
            idCardNumber = (int) in.nval;
            setAveragePoint(averagePoint);
            setName(name);
            setGraduateSubscript(graduateSubscript);
            setIdCardNumber(idCardNumber);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WrongAveragePoint wrongAveragePoint) {
            wrongAveragePoint.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return idCardNumber+",'"+name+"',"+"'"+graduateSubscript+"',"+averagePoint;
    }

    
}
