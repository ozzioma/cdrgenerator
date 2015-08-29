package com.ozzy.bcsproject.cdrgenerator;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Date;
import org.joda.time.*;

//State,City,

@AutoProperty
public class CdrInfo
{

    //customer details
    public String customerId;
    public String customerPhoneNumber;

//    public String customerName;
//    public String address;
//    public String city;
//    public String state;


    //call details
    public Date callBegin;
    public Date callEnd;
    public String callType;
    public String serviceType;
    public String serviceDescription;
    public String callPlan;
    public String dataPlan;
    public double ratePerSec;
    public double smsRate;
    public double dataRate;
    public double dataKiloBytes;

    //caller details
    public String inPhoneNumber;
    public String outPhoneNumber;
    public String smsText;


    public String getCustomerId()
    {
        return customerId;
    }

    public void setCustomerId(String customerId)
    {
        this.customerId = customerId;
    }

    public String getCustomerPhoneNumber()
    {
        return customerPhoneNumber;
    }

    public void setCustomerPhoneNumber(String customerPhoneNumber)
    {
        this.customerPhoneNumber = customerPhoneNumber;
    }


//    public String getCustomerName()
//    {
//        return customerName;
//    }
//
//    public void setCustomerName(String customerName)
//    {
//        this.customerName = customerName;
//    }
//
//
//    public String getAddress()
//    {
//        return address;
//    }
//
//    public void setAddress(String address)
//    {
//        this.address = address;
//    }
//
//    public String getCity()
//    {
//        return city;
//    }
//
//    public void setCity(String city)
//    {
//        this.city = city;
//    }
//
//    public String getState()
//    {
//        return state;
//    }
//
//    public void setState(String state)
//    {
//        this.state = state;
//    }

    public Date getCallBegin()
    {
        return callBegin;
    }

    public void setCallBegin(Date callBegin)
    {
        this.callBegin = callBegin;
    }

    public Date getCallEnd()
    {
        return callEnd;
    }

    public void setCallEnd(Date callEnd)
    {
        this.callEnd = callEnd;
    }

    public String getCallType()
    {
        return callType;
    }

    public void setCallType(String callType)
    {
        this.callType = callType;
    }

    public String getServiceType()
    {
        return serviceType;
    }

    public void setServiceType(String serviceType)
    {
        this.serviceType = serviceType;
    }

    public String getServiceDescription()
    {
        return serviceDescription;
    }

    public void setServiceDescription(String serviceDescription)
    {
        this.serviceDescription = serviceDescription;
    }

    public String getCallPlan()
    {
        return callPlan;
    }

    public void setCallPlan(String callPlan)
    {
        this.callPlan = callPlan;
    }

    public String getDataPlan()
    {
        return dataPlan;
    }

    public void setDataPlan(String dataPlan)
    {
        this.dataPlan = dataPlan;
    }

    public double getRatePerSec()
    {
        return ratePerSec;
    }

    public void setRatePerSec(double ratePerSec)
    {
        this.ratePerSec = ratePerSec;
    }

    public double getSmsRate()
    {
        return smsRate;
    }

    public void setSmsRate(double smsRate)
    {
        this.smsRate = smsRate;
    }

    public double getDataRate()
    {
        return dataRate;
    }

    public void setDataRate(double dataRate)
    {
        this.dataRate = dataRate;
    }

    public double getDataKiloBytes()
    {
        return dataKiloBytes;
    }

    public void setDataKiloBytes(double dataKiloBytes)
    {
        this.dataKiloBytes = dataKiloBytes;
    }

    public String getSmsText()
    {
        return smsText;
    }

    public void setSmsText(String smsText)
    {
        this.smsText = smsText;
    }

    public String getInPhoneNumber()
    {
        return inPhoneNumber;
    }

    public void setInPhoneNumber(String inPhoneNumber)
    {
        this.inPhoneNumber = inPhoneNumber;
    }

    public String getOutPhoneNumber()
    {
        return outPhoneNumber;
    }

    public void setOutPhoneNumber(String outPhoneNumber)
    {
        this.outPhoneNumber = outPhoneNumber;
    }

    @Override public boolean equals(Object o) {
        return Pojomatic.equals(this, o);
    }

    @Override public int hashCode() {
        return Pojomatic.hashCode(this);
    }

    @Override public String toString() {
        return Pojomatic.toString(this);
    }
}

class CallType
{

}

class TelecomService
{
    public String type;
    public String service;
    public String description;
    public double serviceRate;

    public TelecomService(String _type,String _service,String _desc)
    {
        type=_type;
        service=_service;
        description=_desc;
        serviceRate=10;
    }

    public TelecomService(String _type,String _service,String _desc,double rate)
    {
        type=_type;
        service=_service;
        description=_desc;
        serviceRate = rate;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getService()
    {
        return service;
    }

    public void setService(String service)
    {
        this.service = service;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public double getServiceRate()
    {
        return serviceRate;
    }

    public void setServiceRate(double serviceRate)
    {
        this.serviceRate = serviceRate;
    }
}
