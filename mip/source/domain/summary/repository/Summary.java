package com.inzisoft.mobileid.sp.domain.summary.repository;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Entity
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "mip_emp_daily_smry",
        uniqueConstraints = {@UniqueConstraint(
                name = "uk_summary_employeeNumber_trxDate",
                columnNames = {"employee_no", "trx_Date", "id_type"}
        )})
public class Summary {
    @Transient
    private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Id
    @Column(name = "ID")
    private int id;
    @Column(name = "employee_no")
    private String employeeNumber;
    @Column(name = "employee_name")
    private String employeeName;
    @Column(name = "trx_date")
    private String trxDate;
    @Column(name ="id_type")
    private String idType;
    @Column(name = "branch_code")
    private String branchCode;
    @Column(name = "branch_name")
    private String branchName;
    @Column(name = "tx_open_count")
    private String txOpenCount;
    @Column(name = "vp_verify_count")
    private String vpVerifyCount;
    @Column(name = "complete_count")
    private String completeCount;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Summary that = (Summary) o;
        return employeeNumber.equals(that.employeeNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeNumber);
    }

    public void setTrxDate(LocalDateTime localDateTime) {
        this.trxDate = DATE_TIME_FORMATTER.format(localDateTime);
    }


}
