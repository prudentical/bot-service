package com.prudentical.botservice.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.prudentical.botservice.persistence.IntegerListConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Entity
@Table(name = "spot_grid_bots")
@PrimaryKeyJoinColumn(name = "id")
public class SpotGridBot extends Bot {

    @PositiveOrZero
    @Column(name = "pair_id", nullable = false)
    private Long pairId;

    @Min(2)
    @Column(name = "grids", nullable = false)
    private int grids;

    @Convert(converter = IntegerListConverter.class)
    @Column(name = "open_position_grids")
    @Builder.Default
    private List<Integer> openPositionGrids = new ArrayList<>();

    @NotNull
    @Column(name = "ceiling", nullable = false)
    private BigDecimal ceiling;

    @NotNull
    @Column(name = "floor", nullable = false)
    private BigDecimal floor;

}
