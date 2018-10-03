package com.spring.phone.repository.jpa;


import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import static java.util.Objects.isNull;

@Data
@EqualsAndHashCode(of = {"pk"})
@Entity
@Table(name = "LINKAGE_TBL")
public class LinkageData {

    @EmbeddedId
    private LinkageDataPK pk;

    @Column(name = "PHONE_ID", length = 255)
    private String phoneId;

    @Column(name = "TIME_STAMP")
    private Date timestamp;

    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;

    @Column(name = "PHONE_HASH")
    private String phoneHash;


    public BigInteger getId() {
        return isNull(pk) ? null : pk.getId();
    }

    public Integer getPartitionKey() {
        return isNull(pk) ? null : pk.getPartitionKey();
    }

    @Data
    @EqualsAndHashCode(of = {"id", "partitionKey"})
    @Embeddable
    static class LinkageDataPK implements Serializable{
        @Column(name = "ID")
        private BigInteger id;

        @Column(name = "PARTITION_KEY")
        private Integer partitionKey;
    }

}
