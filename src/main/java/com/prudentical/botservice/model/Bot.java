package com.prudentical.botservice.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity
@Table(name = "bots", indexes = {
        @Index(name = "idx_bots_account_id", columnList = "account_id") }, uniqueConstraints = {
                @UniqueConstraint(name = "uq_bots_title_per_account",columnNames = { "account_id", "title" }) })
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = Bot.DISCRIMINATOR_COLUMN, discriminatorType = DiscriminatorType.STRING, length = 50)
public abstract class Bot extends BaseModel<Long> {

    public static final String DISCRIMINATOR_COLUMN = "type";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @PositiveOrZero
    @EqualsAndHashCode.Include
    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @PositiveOrZero
    @EqualsAndHashCode.Include
    @Column(name = "exchange_id", nullable = false)
    private Long exchangeId;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Positive
    @Column(name = "capital", nullable = false)
    private BigDecimal capital;

    @Column(name = "take_profit")
    private BigDecimal takeProfit;

    @Column(name = "stop_loss")
    private BigDecimal stopLoss;

}
