package com.TrOps.mvp.client.model;

import com.TrOps.mvp.common.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "clients")
@Getter
@Setter
public class Client extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column
    private String email;

    @Column
    private String phone;
}
