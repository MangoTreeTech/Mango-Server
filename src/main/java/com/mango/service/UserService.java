package com.mango.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mango.entity.User;

public interface UserService extends IService<User> {

    /**
     * 根据id将文件名fileName更新指定用户的icon属性
     *
     * @param fileName
     * @param id
     */
    void uploadHeadById(String fileName, Integer id);

    /**
     * 判断用户id是否具有唯一性
     *
     * @param id
     * @return
     */
    boolean isIdUnique(Integer id);

    boolean isPasswordUnique(Integer password);

    /**
     * 判断是否存在这样的id和password
     *
     * @param id
     * @param password
     * @return
     */
    boolean existIdAndPassword(Integer id, Integer password);

    /**
     * 更新用户位置属性
     * @param id
     * @param posX
     * @param posY
     */
    void uploadLocationById(Integer id, double posX, double posY);
}
