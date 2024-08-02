package com.fiafeng.flowable.flowable.dao;


import com.fiafeng.flowable.flowable.domain.SysForm;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 流程表单Mapper接口
 * 
 * @author Tony
 * @date 2021-03-30
 */
@Mapper
public interface SysFormMapper
{
    /**
     * 查询流程表单
     * 
     * @param formId 流程表单ID
     * @return 流程表单
     */
    public SysForm selectSysFormById(Long formId);

    /**
     * 查询流程表单列表
     * 
     * @param sysForm 流程表单
     * @return 流程表单集合
     */
    public List<SysForm> selectSysFormList(SysForm sysForm);

    /**
     * 新增流程表单
     * 
     * @param sysForm 流程表单
     * @return 结果
     */
    public int insertSysForm(SysForm sysForm);

    /**
     * 修改流程表单
     * 
     * @param sysForm 流程表单
     * @return 结果
     */
    public int updateSysForm(SysForm sysForm);

    /**
     * 删除流程表单
     * 
     * @param formId 流程表单ID
     * @return 结果
     */
    public int deleteSysFormById(Long formId);

    /**
     * 批量删除流程表单
     * 
     * @param formIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteSysFormByIds(Long[] formIds);
}
