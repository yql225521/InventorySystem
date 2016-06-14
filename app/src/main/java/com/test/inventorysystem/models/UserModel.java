package com.test.inventorysystem.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by youmengli on 6/6/16.
 */
@DatabaseTable(tableName = "user")
public class UserModel {

    @DatabaseField(id = true)
    private String accounts;
    @DatabaseField
    private String username;
    @DatabaseField
    private String isValid;
    @DatabaseField
    private String password;
    @DatabaseField
    private String departmentId;

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public String getAccounts() {
        return accounts;
    }

    public void setAccounts(String accounts) {
        this.accounts = accounts;
    }

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("accounts: ").append(accounts);
        sb.append(", ").append("username: ").append(username);
        sb.append(", ").append("isValid: ").append(isValid);
        sb.append(", ").append("departmentId: ").append(departmentId);

        return sb.toString();
    }
}
