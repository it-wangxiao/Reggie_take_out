package com.itheima.reggie.web.controller;


import com.itheima.reggie.utils.FileUtil;
import com.itheima.reggie.web.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @author wx
 * @version 1.0
 * @date 2022/6/11 16:53
 * 处理文件上传下载的控制器
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    /**
     * * 图片上传。上传到配置文件中指定的目录。
     * ***使用UUID重命名文件，避免同名文件覆盖问题
     * ***随机创建三级目录，避免单文件夹中文件过多造成卡顿
     *
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) throws IOException {

        // 1. 获取文件的原始文件名，通过原始文件名获取文件后缀
        String ofn = file.getOriginalFilename();
        String[] strs = ofn.split("\\.");
        String str = strs[strs.length - 1];

        // 2. 使用`UUID`生成新的文件名，避免重名；
        String fileNameWithPath = FileUtil.getFileNameWithPath();

        // 3. 通过随机算法分配创建多级目录，避免一个目录中存放过多文件
        FileUtil.makeDirs(fileNameWithPath, basePath);

        // 4. 将上传的临时文件转存到指定位置
        file.transferTo(new File(basePath, fileNameWithPath + "." + str));

        // 5. 组织数据，响应数据
        return R.success("上传成功", fileNameWithPath + "." + str);

    }

    /**
     * 下载文件，如果图片不存在，则不响应任何内容
     *
     * @param name
     * @param response
     * @throws IOException
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) throws IOException {

        //1. 定义输入流，关联拼接后的对应文件。如果图片文件不存在，就直接结束请求
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(new File(basePath, name)));
        } catch (FileNotFoundException e) {
            log.warn("图片文件找不到");
            e.printStackTrace();
        }

        //2. 通过`response`对象，获取到输出流
        ServletOutputStream os = null;
        try {
            os = response.getOutputStream();
        } catch (IOException e) {
            log.info("响应图片失败");
            e.printStackTrace();
        }

        //3. 通过`response`对象设置响应数据格式(`image/jpeg`)
        response.setContentType("image/jpeg");

        //4. 通过输入流读取文件数据，然后通过上述的输出流写回浏览器
        byte[] bytes = new byte[1024];
        int len = 0;
        while ((len = bis.read(bytes)) > 0) {
            os.write(bytes, 0, len);
            os.flush();
        }

        //5. 关闭资源
        bis.close();

    }




/*    @Value("${reggie.path}")
    private String basePath;


    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) throws IOException {

        String ofn = file.getOriginalFilename();
        String[] strings = ofn.split("\\.");

        String string = strings[strings.length - 1];

        String fileNameWithPath = FileUtil.getFileNameWithPath();

        FileUtil.makeDirs(fileNameWithPath,basePath);

        file.transferTo(new File(basePath,fileNameWithPath+"."+string));


        return R.success("上传成功",fileNameWithPath+"."+string);
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) throws IOException {

        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(new File(basePath,name)));
        } catch (FileNotFoundException e) {
            log.info("文件不存在");
            e.printStackTrace();
        }

        ServletOutputStream os = null;
        try {
            os = response.getOutputStream();
        } catch (IOException e) {
            log.info("响应数据失败");
            e.printStackTrace();
        }

        response.setContentType("image/jpeg");

        byte[] bytes = new byte[1024];
        int len = 0;

        while ((len = bis.read(bytes))>0){

            os.write(bytes,0,len);
            os.flush();
        }
        bis.close();
    }
*/
}
