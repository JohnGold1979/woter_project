package com.example.WOTER;

import jakarta.persistence.*;

@Entity
@Table(name = "wot_saldo")
public class SaldoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // предполагаю, что в wot_saldo есть PK id

    // можно ничего больше не добавлять, если мы будем использовать только nativeQuery
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
