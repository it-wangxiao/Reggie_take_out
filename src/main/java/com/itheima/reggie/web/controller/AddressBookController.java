package com.itheima.reggie.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.itheima.reggie.entity.AddressBook;
import com.itheima.reggie.service.AddressBookService;
import com.itheima.reggie.utils.BaseContextUtil;
import com.itheima.reggie.web.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * @author wx
 * @version 1.0
 * @date 2022/6/13 17:09
 */
@RestController
@RequestMapping("/addressBook")
@Slf4j
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

/*    @Autowired
    private HttpSession session;*/

    /**
     * 地址管理查询
     *
     * @return
     */
    @GetMapping("/list")
    public R<List<AddressBook>> findList() {

        //获取用户Id
//        Long userId = (Long) session.getAttribute("userId");
        Long userId = BaseContextUtil.getCurrentId();


        LambdaQueryWrapper<AddressBook> qw = new LambdaQueryWrapper<>();
        qw.eq(AddressBook::getUserId, userId)
                .orderByDesc(AddressBook::getIsDefault)
                .orderByDesc(AddressBook::getUpdateTime);
        List<AddressBook> addressBooks = addressBookService.list(qw);

        return R.success("查找成功", addressBooks);
    }

    /**
     * 添加地址
     *
     * @param addressBook
     * @return
     */
    @PostMapping
    public R save(@RequestBody AddressBook addressBook) {

//        Long userId = (Long) session.getAttribute("userId");
        Long userId = BaseContextUtil.getCurrentId();

        addressBook.setUserId(userId);
        boolean save = addressBookService.save(addressBook);

        if (save) {
            return R.success("添加成功");
        }
        return R.error("添加失败");
    }

    /**
     * 设置默认地址
     *
     * @param addressBook
     * @return
     */
    @PutMapping("default")
    public R addressDefault(@RequestBody AddressBook addressBook) {

        LambdaUpdateWrapper<AddressBook> luw = new LambdaUpdateWrapper<>();
        luw.eq(AddressBook::getUserId, BaseContextUtil.getCurrentId())
                .set(AddressBook::getIsDefault, 0);
        addressBookService.update(luw);
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return R.success("修改成功");
    }


    /**
     * 地址修改
     * 数据回显
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<AddressBook> getAddress(@PathVariable Long id) {

        if (id != null) {
            AddressBook addressBook = addressBookService.getById(id);
            if (addressBook != null) {
                return R.success("回显成功", addressBook);
            }
            return R.error("回显失败");
        }
        return R.error("参数有误");
    }


    /**
     * 地址修改
     *
     * @param addressBook
     * @return
     */
    @PutMapping
    public R update(@RequestBody AddressBook addressBook) {
        if (addressBook.getId() != null) {
            addressBookService.updateById(addressBook);
            return R.success("修改成功");
        }
        return R.error("参数有误");
    }

    /**
     * 删除地址
     * @param ids
     * @return
     */
    @DeleteMapping
    public R delete(Long[] ids){

        addressBookService.removeByIds(Arrays.asList(ids));

        return R.success("删除成功");
    }

    @GetMapping("default")
    public R<AddressBook> getDefault(){

        LambdaQueryWrapper<AddressBook> qw = new LambdaQueryWrapper<>();
        qw.eq(AddressBook::getIsDefault,1);
        AddressBook defaultAddressBook = addressBookService.getOne(qw);

        if (defaultAddressBook!=null) {
            return R.success("成功", defaultAddressBook);
        }
        return R.error("失败");
    }
}
