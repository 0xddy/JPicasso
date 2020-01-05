package cn.lmcw.jpicasso.service.base;

import java.io.File;
import java.io.InputStream;

public interface IService {

    public void compress(File inFile, File outFile) throws Exception;

    public void syncYun(File file, String path) throws Exception;

    public void check(String localPath,String yunKey);

    public void deleteYunFile(String file);

}
