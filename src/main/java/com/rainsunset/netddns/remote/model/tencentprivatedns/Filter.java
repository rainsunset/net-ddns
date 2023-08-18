package com.rainsunset.netddns.remote.model.tencentprivatedns;

import com.tencentcloudapi.common.AbstractModel;
import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Expose;
import java.util.HashMap;

public class Filter extends AbstractModel{

    /**
     * 参数名
     */
    @SerializedName("Name")
    @Expose
    private String Name;

    /**
     * 参数值数组
     */
    @SerializedName("Values")
    @Expose
    private String [] Values;

    /**
     * Get 参数名
     * @return Name 参数名
     */
    public String getName() {
        return this.Name;
    }

    /**
     * Set 参数名
     * @param Name 参数名
     */
    public void setName(String Name) {
        this.Name = Name;
    }

    /**
     * Get 参数值数组
     * @return Values 参数值数组
     */
    public String [] getValues() {
        return this.Values;
    }

    /**
     * Set 参数值数组
     * @param Values 参数值数组
     */
    public void setValues(String [] Values) {
        this.Values = Values;
    }

    public Filter() {
    }

    /**
     * NOTE: Any ambiguous key set via .set("AnyKey", "value") will be a shallow copy,
     *       and any explicit key, i.e Foo, set via .setFoo("value") will be a deep copy.
     */
    public Filter(Filter source) {
        if (source.Name != null) {
            this.Name = new String(source.Name);
        }
        if (source.Values != null) {
            this.Values = new String[source.Values.length];
            for (int i = 0; i < source.Values.length; i++) {
                this.Values[i] = new String(source.Values[i]);
            }
        }
    }


    /**
     * Internal implementation, normal users should not use it.
     */
    public void toMap(HashMap<String, String> map, String prefix) {
        this.setParamSimple(map, prefix + "Name", this.Name);
        this.setParamArraySimple(map, prefix + "Values.", this.Values);

    }
}
