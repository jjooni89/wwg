package com.project.wwg.plan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Plan {
    private String username;
    private int idx;
    private String title;
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date departure;
    private int days;
    private String plans;
    private int hit;
    private int good;
    private Date regDate;
    private int pub;
}
