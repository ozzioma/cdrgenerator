/**
 * Created by Ozzy on 016, Aug 16, 2015.
 */
package com.ozzy.bcsproject.cdrgenerator;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.github.javafaker.Address;
import com.github.javafaker.Faker;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import io.codearte.jfairy.Fairy;
import io.codearte.jfairy.producer.person.Person;
import org.fluttercode.datafactory.impl.DataFactory;
import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;


public class App
{
    static Map<String, String> states = new HashMap<String, String>();

    public static void main( String[] args )
    {
        initData();

        //SaveCustomers();

        SaveCdrs();
        //demoTime();

    }


    static List<Customer> genCustomers(int vol)
    {

        List<String> _states = new ArrayList<String>();
        states.forEach((k, v) -> _states.add(k));

        Map<String, String> newStates = new HashMap<>();
        for(Map.Entry<String, String> entry : states.entrySet()){
            newStates.put(entry.getValue(), entry.getKey());
        }

        List<Customer> customers=new ArrayList<>();

        Fairy faker=Fairy.create();
        DataFactory factory=new DataFactory();
        for (int counter=0;counter <vol;counter++)
        {
            Person person=faker.person();

            Customer cust=new Customer();
            cust.setCustomerId(person.nationalIdentityCardNumber());
            cust.setCustomerName(person.fullName());
            cust.setAddress(person.getAddress().toString());
            cust.setCity(person.getAddress().getCity());
            cust.setState(factory.getItem(_states));

            cust.setPhoneNumber(person.telephoneNumber());

            customers.add(cust);
        }
        return customers;
    }

    static List<CdrInfo> generateRecords(List<Customer> customers,int year,int month)
    {

        List<CdrInfo> records=new ArrayList<>();

        List<String> _states = new ArrayList<String>();
        states.forEach((k, v) -> _states.add(k));

        Map<String, String> newStates = new HashMap<>();
        for(Map.Entry<String, String> entry : states.entrySet()){
            newStates.put(entry.getValue(), entry.getKey());
        }

        Fairy faker=Fairy.create();
        DataFactory factory=new DataFactory();



        String[] callPlans={"Starter","Silver","Gold","Platimum","Elite"};
        String[] dataPlans={"Eazy Surf","Quick Surf","Biz Surf"};
        String[] serviceIds={"0","1","2","3","4","5"};
        List<TelecomService> services=new ArrayList<>();

        services.add(0,new TelecomService("MOC","Voice","Mobile Originated Call (outgoing call)",15));
        services.add(1,new TelecomService("MTC","Voice","Mobile Terminated Call (incoming call)",0));
        services.add(2,new TelecomService("SMS-MO","Text","SMS Mobile Originated (outgoing SMS)",30));
        services.add(3,new TelecomService("SMS-MT","Text","SMS Mobile Terminated (incoming SMS)",0));
        services.add(4,new TelecomService("GPRS","3G","3G Data session",12));
        services.add(5,new TelecomService("GPRS","LTE","LTE Data session",16));


        for (Customer customer:customers)
        {
            System.out.println(customer);
            System.out.println("\n");

            int recordsPerCust=randInt(30,randInt(60,randInt(90,randInt(120,200))));

            System.out.println("CDR total->"+recordsPerCust);

            for (int counter=0;counter <recordsPerCust;counter++)
            {

                String callPlan=factory.getItem(callPlans,75,"Silver");
                String dataPlan=factory.getItem(dataPlans,75,"Eazy Surf");
                //int sid=faker.baseProducer().randomBetween(1,6);
                String sid=factory.getItem(serviceIds,75,"1");
                TelecomService service=services.get(Integer.parseInt(sid));
                String outPhone;

                int year2=randInt(year-3,year+1);
                DateTime beginDate=new DateTime().withYear(year2).withMonthOfYear(randInt(1, randInt(3, randInt(6, randInt(9, 12))))).plusDays(randInt(0, 28))
                    .plusHours(randInt(0, randInt(1, randInt(2, randInt(3, randInt(4, 11)))))).plusMinutes(randInt(1, randInt(2, randInt(4, randInt(8, 59)))));

                DateTime endDate=beginDate.plusMinutes(randInt(1, randInt(2, randInt(4, randInt(8, 59))))).plusSeconds(randInt(1, 59));;


                Person person=faker.person();

                String outSmsNo=faker.person().telephoneNumber();
                String outCallNo=faker.person().telephoneNumber();
                String inSmsNo=faker.person().telephoneNumber();
                String inCallNo=faker.person().telephoneNumber();


                CdrInfo cdr=new CdrInfo();

                //customer details
                cdr.setCustomerId(customer.getCustomerId());
                cdr.setCustomerPhoneNumber(customer.getPhoneNumber());

//                cdr.setCustomerName(customer.getCustomerName());
//                cdr.setAddress(customer.getAddress());
//                cdr.setCity(customer.getCity());
//                cdr.setState(customer.getState());


                cdr.setCallType(service.getType());
                cdr.setServiceType(service.getService());
                cdr.setServiceDescription(service.getDescription());

                if(service.getType()=="MOC")
                {
                    cdr.setOutPhoneNumber(outCallNo);
                    cdr.setCallPlan(callPlan);
                    cdr.setRatePerSec(service.getServiceRate());
                    //call details
                    cdr.setCallBegin(beginDate.toDate());
                    cdr.setCallEnd(endDate.toDate());
                }
                if(service.getType()=="MTC")
                {
                    cdr.setInPhoneNumber(inCallNo);
                    cdr.setCallPlan(callPlan);
                    cdr.setRatePerSec(service.getServiceRate());
                    //call details
                    cdr.setCallBegin(beginDate.toDate());
                    cdr.setCallEnd(endDate.toDate());

                }

                if(service.getType()=="SMS-MO")
                {
                    cdr.setInPhoneNumber(outSmsNo);
                    cdr.setCallPlan(callPlan);
                    cdr.setSmsRate(service.getServiceRate());
                    cdr.setSmsText(faker.textProducer().paragraph(faker.baseProducer().randomBetween(3,15)));
                    cdr.setCallBegin(beginDate.toDate());

                }

                if(service.getType()=="SMS-MT")
                {
                    cdr.setInPhoneNumber(inSmsNo);
                    cdr.setCallPlan(callPlan);
                    cdr.setSmsRate(service.getServiceRate());
                    cdr.setSmsText(faker.textProducer().paragraph(faker.baseProducer().randomBetween(3,15)));
                    cdr.setCallBegin(beginDate.toDate());

                }

                if(service.getService()=="3G" || service.getService()=="LTE")
                {
                    cdr.setCallBegin(beginDate.toDate());
                    DateTime endDate2=beginDate.plusHours(randInt(0, randInt(1, randInt(2, randInt(3, randInt(4, 11)))))).plusMinutes(randInt(1, randInt(5, randInt(10, randInt(15, 59))))).plusSeconds(randInt(1, 59));
                    cdr.setCallEnd(endDate2.toDate());

                    cdr.setDataPlan(dataPlan);
                    cdr.setDataRate(service.getServiceRate());
                    //faker.baseProducer().randomBetween(16 * 32*64*Math.random(), 64 * 128 * 256* Math.random())
                    cdr.setDataKiloBytes(randDouble(2*1024,randDouble(4*8*1024,randDouble(8*16*1024,16*32*1024))));
                }

                //System.out.println(cdr);
                records.add(cdr);

            }

            System.out.println("\n");
        }

        return records;

    }


    public static int randInt(int min, int max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }


    public static double randDouble(double min, double max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        double randomNum = min + rand.nextDouble() * max;

        return randomNum;
    }

    static void SaveCdrs()
    {
        //String custfileName = "F:\\Me Project Docs\\Datasets\\telecom\\bcs\\customers-30k.csv";
        String custfileName = "./customers-30k.csv";
        String basefileName = "F:\\Me Project Docs\\Datasets\\telecom\\bcs\\batch\\";

        List<Customer> customers=new ArrayList<>();
        try
        {

            //File custfile = new File(custfileName);
            ResourceLoader loader=new ResourceLoader(custfileName);

            CsvMapper mapper = new CsvMapper();
            CsvSchema schema = mapper.schemaFor(Customer.class).withColumnSeparator('|').withHeader();
            MappingIterator<Customer> it = null;
            //it=mapper.reader(Customer.class).with(schema).readValues(custfile);
            it=mapper.reader(Customer.class).with(schema).readValues(loader.getResourceStream());

            while (it.hasNext()){
                customers.add(it.next());
            }

            int batch=1;
            int year=2013;
            int month=1;

            for (List<Customer> partition : Lists.partition(customers, 3000)) {

                List<CdrInfo> cdrs= generateRecords(partition,year,month);

                String csvCdr=CdrToCSV(cdrs);

                //String cdrFile=basefileName+"cdr-"+year+"-"+month+"-batch-"+batch+".csv";
                String cdrFile=basefileName+"cdr-batch-"+batch+".csv";

                File file = new File(cdrFile);
                Files.write(csvCdr, file, Charsets.UTF_8);

                System.out.println("Total cdrs->" + cdrs.size());
                System.out.println("Finished batch->"+cdrFile);

                batch += 1;

            }



        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    static void SaveCustomers()
    {
        String basefileName = "F:\\Me Project Docs\\Datasets\\telecom\\bcs\\";
        try
        {
            List<Customer> customers=genCustomers(30000);

            if(customers !=null)
            {
                String csvCustomers=CustomerToCSV(customers);
                //System.out.println(csvQuotes);

                String customerFile=basefileName+"customers-30k.csv";

                File file = new File(customerFile);

                Files.write(csvCustomers, file, Charsets.UTF_8);

                System.out.println("Total customers->"+customers.size());

            }


        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    static void initData()
    {
        states.put("Alabama","AL");
        states.put("Alaska","AK");
        states.put("Alberta","AB");
        states.put("American Samoa","AS");
        states.put("Arizona","AZ");
        states.put("Arkansas","AR");
        //states.put("Armed Forces (AE)","AE");
        //states.put("Armed Forces Americas","AA");
        //states.put("Armed Forces Pacific","AP");
        states.put("British Columbia","BC");
        states.put("California","CA");
        states.put("Colorado","CO");
        states.put("Connecticut","CT");
        states.put("Delaware","DE");
        states.put("District Of Columbia","DC");
        states.put("Florida","FL");
        states.put("Georgia","GA");
        states.put("Guam","GU");
        states.put("Hawaii","HI");
        states.put("Idaho","ID");
        states.put("Illinois","IL");
        states.put("Indiana","IN");
        states.put("Iowa","IA");
        states.put("Kansas","KS");
        states.put("Kentucky","KY");
        states.put("Louisiana","LA");
        states.put("Maine","ME");
        states.put("Manitoba","MB");
        states.put("Maryland","MD");
        states.put("Massachusetts","MA");
        states.put("Michigan","MI");
        states.put("Minnesota","MN");
        states.put("Mississippi","MS");
        states.put("Missouri","MO");
        states.put("Montana","MT");
        states.put("Nebraska","NE");
        states.put("Nevada","NV");
        states.put("New Brunswick","NB");
        states.put("New Hampshire","NH");
        states.put("New Jersey","NJ");
        states.put("New Mexico","NM");
        states.put("New York","NY");
        states.put("Newfoundland","NF");
        states.put("North Carolina","NC");
        states.put("North Dakota","ND");
        states.put("Northwest Territories","NT");
        states.put("Nova Scotia","NS");
        states.put("Nunavut","NU");
        states.put("Ohio","OH");
        states.put("Oklahoma","OK");
        states.put("Ontario","ON");
        states.put("Oregon","OR");
        states.put("Pennsylvania","PA");
        states.put("Prince Edward Island","PE");
        states.put("Puerto Rico","PR");
        states.put("Quebec","PQ");
        states.put("Rhode Island","RI");
        states.put("Saskatchewan","SK");
        states.put("South Carolina","SC");
        states.put("South Dakota","SD");
        states.put("Tennessee","TN");
        states.put("Texas","TX");
        states.put("Utah","UT");
        states.put("Vermont","VT");
        states.put("Virgin Islands","VI");
        states.put("Virginia","VA");
        states.put("Washington","WA");
        states.put("West Virginia","WV");
        states.put("Wisconsin","WI");
        states.put("Wyoming","WY");
        states.put("Yukon Territory","YT");


    }


    public static String CdrToCSV(List<CdrInfo> records)
    {

        String csv = null;
        char[] sep=new char[2];
        sep[0]='|';
        sep[1]='\'';

        try
        {
            CsvMapper mapper = new CsvMapper();
            CsvSchema schema = mapper.schemaFor(CdrInfo.class).withHeader().withoutQuoteChar().withColumnSeparator(sep[0]);

            mapper.addMixInAnnotations(CdrInfo.class, CdrTimeStampFormat.class);
            csv = mapper.writer(schema).writeValueAsString(records);
        } catch (JsonProcessingException e)
        {
            e.printStackTrace();
        }
        return csv;
    }

    public static String CustomerToCSV(List<Customer> records)
    {

        String csv = null;
        try
        {
            CsvMapper mapper = new CsvMapper();
            CsvSchema schema = mapper.schemaFor(Customer.class).withHeader();

            mapper.addMixInAnnotations(Customer.class, CdrTimeStampFormat.class);
            csv = mapper.writer(schema).writeValueAsString(records);
        } catch (JsonProcessingException e)
        {
            e.printStackTrace();
        }
        return csv;
    }

    public abstract class CdrTimeStampFormat
    {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "YYYY-MM-dd HH:mm:ss.SSS")
        abstract java.sql.Timestamp getCallBegin();

        //        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "YYYY-MM-dd HH:mm:ss.SSS")
        abstract java.sql.Timestamp getCallEnd();
    }


    static void demoTime()
    {
        Fairy faker=Fairy.create();

        for (int count=0;count<200;count++)
        {
            try
            {


                int randomMills=faker.baseProducer().randomBetween(30 * 60*90 , 30*60*90 * 10);
                int randomMills2=faker.baseProducer().randomBetween(30 * 60*90*30 , 30*60*90 *120);

                long start2012 = new SimpleDateFormat("yyyy").parse("2012").getTime();
                System.out.println("Start time->" + start2012);

                final long millisInYear2012 = 1000 * 60 * 60 * 24 * 365 + 1000; // Have to account for the leap second!
                long millis = Math.round(millisInYear2012 * Math.random());
                Timestamp timeStamp = new Timestamp(start2012 + millis);
                DateTime beginTime=new DateTime(timeStamp);

                //=new DateTime(new Timestamp(start2012 + millis));

                DateTime time1=new DateTime().withYear(2012).withMonthOfYear(randInt(1, randInt(3, randInt(6, randInt(9, 12))))).plusDays(randInt(0, 28))
                    .plusHours(randInt(0, randInt(1, randInt(2, randInt(3, randInt(4, 11)))))).plusMinutes(randInt(1, randInt(2, randInt(4, randInt(8, 59)))));

                DateTime time2;//=new DateTime(new Timestamp(start2012 + millis+randomMills));
                time2=time1.plusMinutes(randInt(1, randInt(2, randInt(4, randInt(8, 59))))).plusSeconds(randInt(1, 59));

                //new DateTime(new Timestamp(start2012 + millis+randomMills2));
                DateTime time3=time1.plusHours(randInt(0, randInt(1, randInt(2, randInt(3, randInt(4, 11)))))).plusMinutes(randInt(1, randInt(5, randInt(10, randInt(15, 59))))).plusSeconds(randInt(1, 59));

                System.out.println("Begin date->" + time1);
                System.out.println("End date->" + time2);
                System.out.println("End date data plan->" + time3);

                Period period=new Period(time1,time2);
                Period period2=new Period(time1,time3);

                System.out.println("Diff seconds->" + period.getSeconds());
                System.out.println("Diff minutes->" + period.getMinutes());
                System.out.println("Diff hrs->" + period.getHours());

                System.out.println("Diff seconds2->" + period2.getSeconds());
                System.out.println("Diff minutes2->" + period2.getMinutes());
                System.out.println("Diff hrs2->" + period2.getHours());


                DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                DateFormat format2 = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss.SSS");
                DateFormat format3 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");

                timeStamp=new Timestamp(time1.getMillis());
                System.out.println("Hive format->" +format1.format(timeStamp));
                System.out.println("Hive format->" +format2.format(timeStamp));
                System.out.println("Hive format->" +format3.format(timeStamp));

                System.out.println("\n");


            } catch (ParseException e)
            {
                e.printStackTrace();
            }


        }

    }


    static void testCustomers(int vol)
    {
        List<String> _states = new ArrayList<String>();
        states.forEach((k, v) -> _states.add(k));

        Map<String, String> newStates = new HashMap<>();
        for(Map.Entry<String, String> entry : states.entrySet()){
            newStates.put(entry.getValue(), entry.getKey());
        }

        DataFactory gen=new DataFactory();
        Faker faker=new Faker();

        for (int counter=0;counter <vol;counter++)
        {
            Customer cust=new Customer();
            cust.setCustomerId(gen.getNumberText(12));
            cust.setCustomerName(gen.getName());
            Address address=faker.address();
            cust.setAddress(address.streetAddress(false));
            cust.setCity(address.cityPrefix());
            String state=newStates.get(address.stateAbbr());
//            cust.setState(gen.getItem(_states));
            cust.setState(newStates.get(address.stateAbbr()));

            cust.setPhoneNumber(faker.phoneNumber().phoneNumber());
            //cust.setPhoneNumber(gen.getNumberText(10));

            System.out.println(cust);
        }
    }


    static void testCustomers2(int vol)
    {
        List<String> _states = new ArrayList<String>();
        states.forEach((k, v) -> _states.add(k));

        Map<String, String> newStates = new HashMap<>();
        for(Map.Entry<String, String> entry : states.entrySet()){
            newStates.put(entry.getValue(), entry.getKey());
        }

        Fairy faker=Fairy.create();
        DataFactory gen=new DataFactory();

        for (int counter=0;counter <vol;counter++)
        {
            Person person=faker.person();

            Customer cust=new Customer();
            cust.setCustomerId(person.nationalIdentityCardNumber());
            cust.setCustomerName(person.fullName());
            cust.setAddress(person.getAddress().toString());
            cust.setCity(person.getAddress().getCity());
            cust.setState(gen.getItem(_states));

            //cust.setState(person.getAddress().);


            cust.setPhoneNumber(person.telephoneNumber());
            //cust.setPhoneNumber(gen.getNumberText(10));

            System.out.println(cust);
        }
    }



}

