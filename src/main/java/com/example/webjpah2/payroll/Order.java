package com.example.webjpah2.payroll;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "CUSTOMER_ORDER")
public class Order {

    private @Id @GeneratedValue Long id;
    private String desc;
    private Status status;

    public Order() {
    }

    public Order(String desc, Status status) {
        this.desc = desc;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id) &&
                Objects.equals(desc, order.desc) &&
                Objects.equals(status, order.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, desc, status);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", desc='" + desc + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
