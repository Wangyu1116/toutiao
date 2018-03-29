package com.nowcoder.service;

import com.nowcoder.dao.NewsDao;
import com.nowcoder.model.News;
import com.nowcoder.util.ToutiaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;


//service层就是调dao层
@Service
public class NewsService {
    @Autowired
    private NewsDao newsDao;

    //最新的新闻读取出来，服务定义的接口通用些
    public List<News> getLatestNews(int userId, int offset, int limit) {
        return newsDao.selectByUserIdAndOffset(userId, offset, limit);
    }

    public News getById(int newsId) {
        return newsDao.getById(newsId);
    }

    //存图片，一些二进制的存储，把名字命好，然后存储到自己的服务器上
    public String saveImage(MultipartFile file) throws IOException {
        int dotPos = file.getOriginalFilename().lastIndexOf(".");   //找到那个点
        if (dotPos < 0) {
            return null;
        }
        String fileExt = file.getOriginalFilename().substring(dotPos + 1).toLowerCase();  //判断是否是有效的名字
        if (!ToutiaoUtil.isFileAllowed(fileExt)) {                                        // 后缀名是否一样
            return null;
        }

        //文件名全部重新命名，加上最后的扩展名
        String fileName = UUID.randomUUID().toString().replaceAll("-", "") + "." + fileExt;
        Files.copy(file.getInputStream(), new File(ToutiaoUtil.IMAGE_DIR + fileName).toPath(),
                StandardCopyOption.REPLACE_EXISTING);   // copy的参数，  第一歌 fileinput就是读入，  如果存在就替换掉

        //都已经保存本地了， 接着返回一个接口给前端
        return ToutiaoUtil.TOUTIAO_DOMAIN + "image?name=" + fileName;
    }
}
