package xmax.crs;

public class CityInfo
{
 private String CityCode;
 public String  CityName;
 public String  StateName;
 public String  StateCode;
 public String  CountryName;
 public String  CountryCode;
 public String  TimeZone;

 public CityInfo(final String aCityCode)
   {
   CityCode = aCityCode;
   }

 public String getCityCode()
   {
   return(CityCode);
   }

}
