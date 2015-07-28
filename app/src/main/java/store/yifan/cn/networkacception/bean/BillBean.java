package store.yifan.cn.networkacception.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author: Jwf(feijia101@gmail.com) <br\>
 * Date: 2015-07-13 16:24<br\>
 * Version: 1.0<br\>
 * Desc:<br\>
 * Revise:<br\>
 */
public class BillBean implements Parcelable {

    private String TER_CODE;//码头
    private String OP_MODE_NAME;//作业方式
    private String BILL_NUM;//提单号
    private String CST_CODE;//客户代码
    private double RN;//
    private String CST_NAME;//申请单位
    private String O_VESSEL_VOYAGE;//出场船名&航次
    private String PAYED_AT;//付费时间
    private String EMP_ID;//受理人
    private String OP_MODE_CODE;//作业方式代码
    private String PAY_NUM;//支付号
    private String I_VESSEL_VOYAGE;//进场船名&航次
    private double TOTAL_AMOUNT;//金额
    private int PLAN_NUM;//受理计划号
    private String PLANED_AT;//计划时间

    public void setTER_CODE(String TER_CODE) {
        this.TER_CODE = TER_CODE;
    }

    public void setOP_MODE_NAME(String OP_MODE_NAME) {
        this.OP_MODE_NAME = OP_MODE_NAME;
    }

    public void setBILL_NUM(String BILL_NUM) {
        this.BILL_NUM = BILL_NUM;
    }

    public void setCST_CODE(String CST_CODE) {
        this.CST_CODE = CST_CODE;
    }

    public void setRN(double RN) {
        this.RN = RN;
    }

    public void setCST_NAME(String CST_NAME) {
        this.CST_NAME = CST_NAME;
    }

    public void setO_VESSEL_VOYAGE(String O_VESSEL_VOYAGE) {
        this.O_VESSEL_VOYAGE = O_VESSEL_VOYAGE;
    }

    public void setPAYED_AT(String PAYED_AT) {
        this.PAYED_AT = PAYED_AT;
    }

    public void setEMP_ID(String EMP_ID) {
        this.EMP_ID = EMP_ID;
    }

    public void setOP_MODE_CODE(String OP_MODE_CODE) {
        this.OP_MODE_CODE = OP_MODE_CODE;
    }

    public void setPAY_NUM(String PAY_NUM) {
        this.PAY_NUM = PAY_NUM;
    }

    public void setI_VESSEL_VOYAGE(String I_VESSEL_VOYAGE) {
        this.I_VESSEL_VOYAGE = I_VESSEL_VOYAGE;
    }

    public void setTOTAL_AMOUNT(double TOTAL_AMOUNT) {
        this.TOTAL_AMOUNT = TOTAL_AMOUNT;
    }

    public void setPLAN_NUM(int PLAN_NUM) {
        this.PLAN_NUM = PLAN_NUM;
    }

    public void setPLANED_AT(String PLANED_AT) {
        this.PLANED_AT = PLANED_AT;
    }

    public String getTER_CODE() {
        return TER_CODE;
    }

    public String getOP_MODE_NAME() {
        return OP_MODE_NAME;
    }

    public String getBILL_NUM() {
        return BILL_NUM;
    }

    public String getCST_CODE() {
        return CST_CODE;
    }

    public double getRN() {
        return RN;
    }

    public String getCST_NAME() {
        return CST_NAME;
    }

    public String getO_VESSEL_VOYAGE() {
        return O_VESSEL_VOYAGE;
    }

    public String getPAYED_AT() {
        return PAYED_AT;
    }

    public String getEMP_ID() {
        return EMP_ID;
    }

    public String getOP_MODE_CODE() {
        return OP_MODE_CODE;
    }

    public String getPAY_NUM() {
        return PAY_NUM;
    }

    public String getI_VESSEL_VOYAGE() {
        return I_VESSEL_VOYAGE;
    }

    public double getTOTAL_AMOUNT() {
        return TOTAL_AMOUNT;
    }

    public int getPLAN_NUM() {
        return PLAN_NUM;
    }

    public String getPLANED_AT() {
        return PLANED_AT;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.TER_CODE);
        dest.writeString(this.OP_MODE_NAME);
        dest.writeString(this.BILL_NUM);
        dest.writeString(this.CST_CODE);
        dest.writeDouble(this.RN);
        dest.writeString(this.CST_NAME);
        dest.writeString(this.O_VESSEL_VOYAGE);
        dest.writeString(this.PAYED_AT);
        dest.writeString(this.EMP_ID);
        dest.writeString(this.OP_MODE_CODE);
        dest.writeString(this.PAY_NUM);
        dest.writeString(this.I_VESSEL_VOYAGE);
        dest.writeDouble(this.TOTAL_AMOUNT);
        dest.writeInt(this.PLAN_NUM);
        dest.writeString(this.PLANED_AT);
    }

    public BillBean() {
    }

    protected BillBean(Parcel in) {
        this.TER_CODE = in.readString();
        this.OP_MODE_NAME = in.readString();
        this.BILL_NUM = in.readString();
        this.CST_CODE = in.readString();
        this.RN = in.readDouble();
        this.CST_NAME = in.readString();
        this.O_VESSEL_VOYAGE = in.readString();
        this.PAYED_AT = in.readString();
        this.EMP_ID = in.readString();
        this.OP_MODE_CODE = in.readString();
        this.PAY_NUM = in.readString();
        this.I_VESSEL_VOYAGE = in.readString();
        this.TOTAL_AMOUNT = in.readDouble();
        this.PLAN_NUM = in.readInt();
        this.PLANED_AT = in.readString();
    }

    public static final Parcelable.Creator<BillBean> CREATOR = new Parcelable.Creator<BillBean>() {
        public BillBean createFromParcel(Parcel source) {
            return new BillBean(source);
        }

        public BillBean[] newArray(int size) {
            return new BillBean[size];
        }
    };
}
