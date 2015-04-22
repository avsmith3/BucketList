package edu.ncsu.csc.bucketlist;

/**
 * Created by user on 4/20/2015.
 */
public class EntryBean {
    public long id;
    public String name;
    public double latitude;
    public double longitude;
    public String comment;
    public int rating;
    public int visited;

    @Override
    public String toString()
    {
        return name;
    }
}
