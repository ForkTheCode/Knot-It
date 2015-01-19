package models;
public class Knot {
	public String title;
	public String description;
	public String imageSource;
	public long timestamp;
	public long reminderTimestamp;
    public int isRepeating;
    public long repeatingTime;
	
	public Knot(String title,String description,String imageSource ,
                long timestamp,long reminderTimestamp ,int isRepeating ,long repeatingTime){
			this.title = title;
			this.description = description;
			this.imageSource = imageSource;
			this.timestamp = timestamp;
			this.reminderTimestamp = reminderTimestamp;
			this.isRepeating = isRepeating;
            this.repeatingTime = repeatingTime;
		}
    public Knot(){
        new Knot(null,null,null,0,0,0,0);
    }
}

