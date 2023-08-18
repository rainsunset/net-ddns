package com.rainsunset.netddns.remote.model.tencentdnspod;


import com.tencentcloudapi.common.AbstractModel;
import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Expose;
import java.util.HashMap;

public class RecordCountInfo extends AbstractModel{

    /**
     * 子域名数量
     */
    @SerializedName("SubdomainCount")
    @Expose
    private Long SubdomainCount;

    /**
     * 列表返回的记录数
     */
    @SerializedName("ListCount")
    @Expose
    private Long ListCount;

    /**
     * 总的记录数
     */
    @SerializedName("TotalCount")
    @Expose
    private Long TotalCount;

    /**
     * Get 子域名数量
     * @return SubdomainCount 子域名数量
     */
    public Long getSubdomainCount() {
        return this.SubdomainCount;
    }

    /**
     * Set 子域名数量
     * @param SubdomainCount 子域名数量
     */
    public void setSubdomainCount(Long SubdomainCount) {
        this.SubdomainCount = SubdomainCount;
    }

    /**
     * Get 列表返回的记录数
     * @return ListCount 列表返回的记录数
     */
    public Long getListCount() {
        return this.ListCount;
    }

    /**
     * Set 列表返回的记录数
     * @param ListCount 列表返回的记录数
     */
    public void setListCount(Long ListCount) {
        this.ListCount = ListCount;
    }

    /**
     * Get 总的记录数
     * @return TotalCount 总的记录数
     */
    public Long getTotalCount() {
        return this.TotalCount;
    }

    /**
     * Set 总的记录数
     * @param TotalCount 总的记录数
     */
    public void setTotalCount(Long TotalCount) {
        this.TotalCount = TotalCount;
    }

    public RecordCountInfo() {
    }

    /**
     * NOTE: Any ambiguous key set via .set("AnyKey", "value") will be a shallow copy,
     *       and any explicit key, i.e Foo, set via .setFoo("value") will be a deep copy.
     */
    public RecordCountInfo(RecordCountInfo source) {
        if (source.SubdomainCount != null) {
            this.SubdomainCount = new Long(source.SubdomainCount);
        }
        if (source.ListCount != null) {
            this.ListCount = new Long(source.ListCount);
        }
        if (source.TotalCount != null) {
            this.TotalCount = new Long(source.TotalCount);
        }
    }


    /**
     * Internal implementation, normal users should not use it.
     */
    public void toMap(HashMap<String, String> map, String prefix) {
        this.setParamSimple(map, prefix + "SubdomainCount", this.SubdomainCount);
        this.setParamSimple(map, prefix + "ListCount", this.ListCount);
        this.setParamSimple(map, prefix + "TotalCount", this.TotalCount);

    }
}

