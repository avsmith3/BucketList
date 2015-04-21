package edu.ncsu.csc.bucketlist;

public class BucketBean {
    public long id;
    public String name;
    public String image;

    @Override
    public String toString()
    {
        return name;
    }
}
