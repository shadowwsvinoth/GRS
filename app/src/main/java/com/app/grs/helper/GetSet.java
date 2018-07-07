package com.app.grs.helper;

public class GetSet {

    public static void setIsLogged(boolean isLogged) {
        GetSet.isLogged = isLogged;
    }

    private static boolean isLogged = false;
    private static String userId = null;
    private static String mobileNo = null;
    private static String imageurl = null;
    private static String productid = null;
    private static String productname = null;
    private static String productprice = null;
    private static String productconfig = null;
    private static String productdesc = null;
    private static String userpicurl = null;
    private static String userpic = null;
    private static String name = null;
    private static String email = null;
    private static String city = null;
    private static String state = null;
    private static String address1 = null;
    private static String address2 = null;
    private static String pincode = null;
    private static  String flag = null;
    private static  String subproductid = null;
    private static  String subproductname = null;
    private static  String subproductprice = null;
    private static  String subproductdesc = null;
    private static  String subproductimage = null;
    private static  String subproductrating = null;

    public static void setSubproductid(String subproductid) {
        GetSet.subproductid = subproductid;
    }

    public static String getSubproductname() {
        return subproductname;
    }

    public static void setSubproductname(String subproductname) {
        GetSet.subproductname = subproductname;
    }

    public static String getSubproductprice() {
        return subproductprice;
    }

    public static void setSubproductprice(String subproductprice) {
        GetSet.subproductprice = subproductprice;
    }

    public static String getSubproductdesc() {
        return subproductdesc;
    }

    public static void setSubproductdesc(String subproductdesc) {
        GetSet.subproductdesc = subproductdesc;
    }


    public static String getSubproductimage() {
        return subproductimage;
    }

    public static void setSubproductimage(String subproductimage) {
        GetSet.subproductimage = subproductimage;
    }

    public static String getSubproductrating() {
        return subproductrating;
    }

    public static void setSubproductrating(String subproductrating) {
        GetSet.subproductrating = subproductrating;
    }

    public static String getFlag() {
        return flag;
    }

    public static void setFlag(String flag) {
        GetSet.flag = flag;
    }

    public static String getUserpic() {
        return userpic;
    }

    public static void setUserpic(String userpic) {
        GetSet.userpic = userpic;
    }

    public static String getPincode() {
        return pincode;
    }

    public static void setPincode(String pincode) {
        GetSet.pincode = pincode;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        GetSet.name = name;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        GetSet.email = email;
    }

    public static String getCity() {
        return city;
    }

    public static void setCity(String city) {
        GetSet.city = city;
    }

    public static String getState() {
        return state;
    }

    public static void setState(String state) {
        GetSet.state = state;
    }

    public static String getAddress1() {
        return address1;
    }

    public static void setAddress1(String address1) {
        GetSet.address1 = address1;
    }

    public static String getAddress2() {
        return address2;
    }

    public static void setAddress2(String address2) {
        GetSet.address2 = address2;
    }

    public static String getUserpicurl() {
        return userpicurl;
    }

    public static void setUserpicurl(String userpicurl) {
        GetSet.userpicurl = userpicurl;
    }

    public static String getUserId() {
        return userId;
    }

    public static void setUserId(String userId) {
        GetSet.userId = userId;
    }

    public static String getMobileNo() {
        return mobileNo;
    }

    public static void setMobileNo(String mobileNo) {
        GetSet.mobileNo = mobileNo;
    }

    public static boolean isIsLogged() {
        return isLogged;
    }

    public static String getImageurl() {
        return imageurl;
    }

    public static void setImageurl(String imageurl) {
        GetSet.imageurl = imageurl;
    }

    public static String getProductid() {
        return productid;
    }

    public static void setProductid(String productid) {
        GetSet.productid = productid;
    }

    public static String getProductname() {
        return productname;
    }

    public static void setProductname(String productname) {
        GetSet.productname = productname;
    }

    public static String getProductprice() {
        return productprice;
    }

    public static void setProductprice(String productprice) {
        GetSet.productprice = productprice;
    }

    public static String getProductconfig() {
        return productconfig;
    }

    public static void setProductconfig(String productconfig) {
        GetSet.productconfig = productconfig;
    }

    public static String getProductdesc() {
        return productdesc;
    }

    public static void setProductdesc(String productdesc) {
        GetSet.productdesc = productdesc;
    }

    public static void reset() {

        GetSet.setIsLogged(false);
        GetSet.setMobileNo(null);
        GetSet.setUserId(null);

    }


}
