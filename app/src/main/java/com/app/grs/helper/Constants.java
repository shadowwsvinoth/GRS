package com.app.grs.helper;

import android.content.SharedPreferences;

public class Constants {

    public static String BASE_URL = "http://grs.shadowws.in/jsons/";

    public static String GET_PROFILE = "get_profile.php?";
    public static String POST_PROFILE = "post_profile.php?";
    public static String UPLOAD_PROFILEPIC = "post_profilepic.php?";
    public static String VERIFY_OTP= "verify_otp.php?";
    public static String REGISTER_USER= "register_user.php?";
    public static String LOGIN_USER= "login_user.php?";
    public static String GET_OTP= "get_otp.php?";
    public static String FORGOT_PASSWORD= "forgot_password.php?";
    public static String CHANGE_PASSWORD= "change_password.php?";
    public static String CATEGORY = "get_category.php?";
    public static String SUBCATEGORY = "get_subcategory.php?";
    public static String BANNER = "get_banner.php?";
    public static String PRODUCT = "get_products.php?";
    public static String SUBPRODUCT = "get_subproducts.php?";
    public static String GET_WISHLIST = "get_wishlist.php?";
    public static String ADD_WISHLIST = "addremove_wishlist.php?";
    public static String ADD_CART = "addremove_cart.php?";
    public static String GET_CART = "get_cart.php?";
    public static String CART_COUNT = "cart_count.php?";
    public static String RATING = "post_rating.php?";
    public static String TOTAL_RATING = "total_product_rating.php?";
    public static String GET_REVIEW = "get_review.php?";
    public static String GET_OFFER_DETAILS = "get_offer_details.php?";
    public static String GET_FEATURED_DETAILS = "get_featured_details.php?";
    public static String GET_ALL_FEATURED= "get_featured_full.php?";
    public static String GET_ALL_OFFER= "get_offer_full.php?";
    public static String GET_OFFER_LAST= "get_offer_last.php?";
    public static String GET_SEARCH= "get_search.php?";

    public static String mobileno="mobileno";
    public static String password="password";
    public static String otp="otp";
    public static String cartflag="flag";
    public static String wishlistflag="flag";
    public static String PRODUCT_ID = "product_id";
    public static String CUSTOMER_ID = "customer_id";


    public static String categoryid="";
    public static String subcategoryid = "";
    public static String productid = "";
    public static String subcategoryname = "";
    public static String categoryname="";
    public static String cart="0";

    public static SharedPreferences pref;
    public static SharedPreferences.Editor editor;

}
