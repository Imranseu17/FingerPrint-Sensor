package com.zkteco.silkiddemo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EmployeeMessage {
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("full_name")
    @Expose
    public String name;
    @SerializedName("photo")
    @Expose
    public String photo;
    @SerializedName("cv")
    @Expose
    public String cv;
    @SerializedName("email")
    @Expose
    public String email;
    @SerializedName("phone")
    @Expose
    public String phone;
    @SerializedName("company_id")
    @Expose
    public String company_id;
    @SerializedName("department_id")
    @Expose
    public String department_id;
    @SerializedName("designation_id")
    @Expose
    public String designation_id;
    @SerializedName("employee_type")
    @Expose
    public String employee_type;
    @SerializedName("dob")
    @Expose
    public String dob;
    @SerializedName("gender")
    @Expose
    public String gender;
    @SerializedName("maritial_status")
    @Expose
    public String maritial_status;
    @SerializedName("nid")
    @Expose
    public String nid;
    @SerializedName("passport")
    @Expose
    public Object passport;
    @SerializedName("present_address")
    @Expose
    public String present_address;
    @SerializedName("permanent_address")
    @Expose
    public String permanent_address;
    @SerializedName("division")
    @Expose
    public Object division;
    @SerializedName("district")
    @Expose
    public Object district;
    @SerializedName("police_station")
    @Expose
    public Object police_station;
    @SerializedName("zip_code")
    @Expose
    public Object zip_code;
    @SerializedName("joining_date")
    @Expose
    public String joining_date;
    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("created_at")
    @Expose
    public String created_at;
    @SerializedName("created_by")
    @Expose
    public String created_by;
    @SerializedName("deleted_at")
    @Expose
    public Object deleted_at;
    @SerializedName("deleted_by")
    @Expose
    public Object deleted_by;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getCv() {
        return cv;
    }

    public void setCv(String cv) {
        this.cv = cv;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getDepartment_id() {
        return department_id;
    }

    public void setDepartment_id(String department_id) {
        this.department_id = department_id;
    }

    public String getDesignation_id() {
        return designation_id;
    }

    public void setDesignation_id(String designation_id) {
        this.designation_id = designation_id;
    }

    public String getEmployee_type() {
        return employee_type;
    }

    public void setEmployee_type(String employee_type) {
        this.employee_type = employee_type;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMaritial_status() {
        return maritial_status;
    }

    public void setMaritial_status(String maritial_status) {
        this.maritial_status = maritial_status;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public Object getPassport() {
        return passport;
    }

    public void setPassport(Object passport) {
        this.passport = passport;
    }

    public String getPresent_address() {
        return present_address;
    }

    public void setPresent_address(String present_address) {
        this.present_address = present_address;
    }

    public String getPermanent_address() {
        return permanent_address;
    }

    public void setPermanent_address(String permanent_address) {
        this.permanent_address = permanent_address;
    }

    public Object getDivision() {
        return division;
    }

    public void setDivision(Object division) {
        this.division = division;
    }

    public Object getDistrict() {
        return district;
    }

    public void setDistrict(Object district) {
        this.district = district;
    }

    public Object getPolice_station() {
        return police_station;
    }

    public void setPolice_station(Object police_station) {
        this.police_station = police_station;
    }

    public Object getZip_code() {
        return zip_code;
    }

    public void setZip_code(Object zip_code) {
        this.zip_code = zip_code;
    }

    public String getJoining_date() {
        return joining_date;
    }

    public void setJoining_date(String joining_date) {
        this.joining_date = joining_date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public Object getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(Object deleted_at) {
        this.deleted_at = deleted_at;
    }

    public Object getDeleted_by() {
        return deleted_by;
    }

    public void setDeleted_by(Object deleted_by) {
        this.deleted_by = deleted_by;
    }
}
