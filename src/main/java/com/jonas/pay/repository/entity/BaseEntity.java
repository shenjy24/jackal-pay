package com.jonas.pay.repository.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * BaseEntity
 *
 * @author shenjy
 * @time 2024/1/27 15:03
 */
@Data
public abstract class BaseEntity implements Serializable {
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    /**
     * 最后更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    /**
     * 创建者
     */
    @TableField(fill = FieldFill.INSERT, jdbcType = JdbcType.VARCHAR)
    private Long creator;
    /**
     * 更新者
     */
    @TableField(fill = FieldFill.INSERT_UPDATE, jdbcType = JdbcType.VARCHAR)
    private Long updater;
    /**
     * 是否删除
     */
    @TableLogic
    private Integer deleted;
}
