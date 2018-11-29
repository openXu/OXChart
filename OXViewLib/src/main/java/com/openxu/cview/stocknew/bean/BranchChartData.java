package com.openxu.cview.stocknew.bean;

import java.util.List;

/**
 * autour : xiami
 * date : 2018/12/04 10:54
 * className : BranchChartData
 * version : 1.0
 * description : 分支图数据
 */
public class BranchChartData {

    private String company;
    private List<String> industryList;
    private List<String> companyList;

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public List<String> getIndustryList() {
        return industryList;
    }

    public void setIndustryList(List<String> industryList) {
        this.industryList = industryList;
    }

    public List<String> getCompanyList() {
        return companyList;
    }

    public void setCompanyList(List<String> companyList) {
        this.companyList = companyList;
    }
}