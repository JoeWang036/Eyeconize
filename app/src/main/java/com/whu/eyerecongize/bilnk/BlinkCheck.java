package com.whu.eyerecongize.bilnk;

import static java.lang.Math.sqrt;

import android.util.Log;

import com.huawei.hms.mlsdk.common.MLPosition;
import com.huawei.hms.mlsdk.face.MLFace;
import com.huawei.hms.mlsdk.face.MLFaceShape;

import java.util.List;

public class BlinkCheck {

  public static boolean checkBlink(List<MLFace> faces){

//    Log.d("distance0",leftpoints.get(0).toString());
//    Log.d("distance7",leftpoints.get(7).toString());
    if (faces == null || faces.size() == 0) {
      return false;
    }
    MLFace face=faces.get(0);

    MLFaceShape leftEye = face.getFaceShape(MLFaceShape.TYPE_LEFT_EYE);//左眼轮廓
    MLFaceShape rightEye = face.getFaceShape(MLFaceShape.TYPE_RIGHT_EYE);//右眼轮廓
    if(leftEye!=null&&rightEye!=null){
    List<MLPosition> leftpoints = leftEye.getPoints();
    List<MLPosition> rightpoints = rightEye.getPoints();

    double LV1= euclideanDistance(leftpoints.get(1),leftpoints.get(14));
    double LV2= euclideanDistance(leftpoints.get(2),leftpoints.get(13));
    double LV3= euclideanDistance(leftpoints.get(3),leftpoints.get(12));
    double LV4= euclideanDistance(leftpoints.get(4),leftpoints.get(11));
    double LV5= euclideanDistance(leftpoints.get(5),leftpoints.get(10));
    double LV6= euclideanDistance(leftpoints.get(6),leftpoints.get(9));

    double LH1= euclideanDistance(leftpoints.get(0),leftpoints.get(7));
    double LH2= euclideanDistance(leftpoints.get(15),leftpoints.get(8));

    double RV1=euclideanDistance(rightpoints.get(1),rightpoints.get(14));
    double RV2=euclideanDistance(rightpoints.get(2),rightpoints.get(13));
    double RV3=euclideanDistance(rightpoints.get(3),rightpoints.get(12));
    double RV4=euclideanDistance(rightpoints.get(4),rightpoints.get(11));
    double RV5=euclideanDistance(rightpoints.get(5),rightpoints.get(10));
    double RV6=euclideanDistance(rightpoints.get(6),rightpoints.get(9));

    double RH1=euclideanDistance(rightpoints.get(0),rightpoints.get(7));
    double RH2=euclideanDistance(rightpoints.get(15),rightpoints.get(8));


//      System.out.println("LV1:"+LV1);
//      System.out.println("LV2:"+LV2);
//      System.out.println("LV3:"+LV3);
//      System.out.println("LV4:"+LV4);
//      System.out.println("LV5:"+LV5);
//      System.out.println("LV6:"+LV6);
//
//      System.out.println("LH1:"+LH1);
//      System.out.println("LH2:"+LH2);




    double Leye =(LV1+LV2+LV3+LV4+LV5+LV6)/(3*(LH1+LH2));
    double Reye=(RV1+RV2+RV3+RV4+RV5+RV6)/(3*(RH1+RH2));


    double res=(Leye+Reye)/2*1000;

    Log.d("juli",res+"");

    //Log.d("zhayan",res+"");

    if(res<350){
      //Log.d("zhayan","zhayan");
      return true;
    }
    }
    return false;


  }

  private static double euclideanDistance( MLPosition pointA, MLPosition pointB){
    double x=(pointA.getX()-pointB.getX())*(pointA.getX()-pointB.getX());
    double y=(pointA.getY()-pointB.getY())*(pointA.getY()-pointB.getY());
    return sqrt((x+y));
  }
}
