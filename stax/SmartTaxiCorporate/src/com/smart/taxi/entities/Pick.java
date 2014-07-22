package com.smart.taxi.entities;

public class Pick {
	
	public int icon;
    public String title;
    
    public String journey_id;
 	public String user_id;
	public String UserName;
	public String UserImage;
	public String group_id;
	public String corporateName;
	public String PickupTime;
	public String PickupLocation;
	public String AdditionalMessage;
    
    
    public Pick(){
        super();
    }
    
    public Pick(String journey_id, String user_id, String UserName,String UserImage,String group_id, String corporateName,String PickupTime,String PickupLocation,String AdditionalMessage ) {
        super();
       this.journey_id =journey_id;
       this.user_id =user_id;
       this.UserName=UserName;
       this.UserImage=UserImage;
       this.group_id=group_id;
       this.corporateName=corporateName;
       this.PickupTime=PickupTime;
       this. PickupLocation=PickupLocation;
       this.AdditionalMessage=AdditionalMessage;
    }

}
