package com.zipe.util.file

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.ArrayList
import java.util.Enumeration
import java.util.zip.ZipEntry
import java.util.zip.ZipException
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

object ZipUtils {
    private const val BUFF_SIZE = 1024 * 1024 // 1M Byte

    /**
     * 壓縮目錄
     *
     * @param srcPath         被壓縮的目錄路徑
     * @param destZipFilePath 目標zip文件路徑
     * @param excludeTopDir   是否排除掉頂層目錄
     * @throws IOException
     */
    fun zip(
        srcPath: String,
        destZipFilePath: String,
        excludeTopDir: Boolean
    ) {
        val fos = FileOutputStream(destZipFilePath)
        val zipOut = ZipOutputStream(fos)
        val srcDirFile = File(srcPath)
        if (srcDirFile.isDirectory) {
            if (excludeTopDir) {
                zipDirExcludeTopDir(zipOut, srcDirFile)
            } else {
                zipFile(srcDirFile, srcDirFile.name, zipOut)
            }
        } else {
            zipFile(srcDirFile, srcDirFile.name, zipOut)
        }
        zipOut.close()
        fos.close()
    }

    /**
     * 壓縮目錄時，去掉最頂層的目錄。
     */
    private fun zipDirExcludeTopDir(zipOut: ZipOutputStream, srcDir: File) {
        val subFiles = srcDir.listFiles()
        for (i in subFiles.indices) {
            val subFile = subFiles[i]
            zipFile(subFile, subFile.name, zipOut)
        }
    }

    @Throws(IOException::class)
    private fun zipFile(fileToZip: File, fileName: String, zipOut: ZipOutputStream) {
        if (fileToZip.isDirectory) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(ZipEntry(fileName))
                zipOut.closeEntry()
            } else {
                zipOut.putNextEntry(ZipEntry("$fileName/"))
                zipOut.closeEntry()
            }
            val children = fileToZip.listFiles()
            for (childFile in children) {
                zipFile(childFile, fileName + "/" + childFile.name, zipOut)
            }
            return
        }
        val fis = FileInputStream(fileToZip)
        val zipEntry = ZipEntry(fileName)
        zipOut.putNextEntry(zipEntry)
        val bytes = ByteArray(1024)
        var length: Int
        while (fis.read(bytes).also { length = it } >= 0) {
            zipOut.write(bytes, 0, length)
        }
        fis.close()
    }

    /**
     * 解壓縮一個文件
     *
     * @param zipFile    壓縮文件
     * @param folderPath 解壓縮的目標目錄
     * @throws IOException 當解壓縮過程出錯時拋出
     */
    @Throws(ZipException::class, IOException::class)
    fun upZipFile(zipFile: File?, folderPath: String) {
        val desDir = File(folderPath)
        if (!desDir.exists()) {
            desDir.mkdirs()
        }
        val zf = ZipFile(zipFile)
        val entries: Enumeration<*> = zf.entries()
        while (entries.hasMoreElements()) {
            val entry = entries.nextElement() as ZipEntry
            if (entry.isDirectory) {
                continue
            }
            val inputStream = zf.getInputStream(entry)
            var str = folderPath + File.separator + entry.name
            str = String(str.toByteArray(), Charset.forName("utf-8"))
            val desFile = File(str)
            if (!desFile.exists()) {
                val fileParentDir = desFile.parentFile
                if (!fileParentDir.exists()) {
                    fileParentDir.mkdirs()
                }
                desFile.createNewFile()
            }
            val out: OutputStream = FileOutputStream(desFile)
            val buffer = ByteArray(BUFF_SIZE)
            var realLength: Int
            while (inputStream.read(buffer).also { realLength = it } > 0) {
                out.write(buffer, 0, realLength)
            }
            inputStream.close()
            out.close()
        }
    }

    /**
     * 解壓文件名包含傳入文字的文件
     *
     * @param zipFile      壓縮文件
     * @param folderPath   目標文件夾
     * @param nameContains 傳入的文件匹配名
     * @throws ZipException 壓縮格式有誤時拋出
     * @throws IOException  IO錯誤時拋出
     */
    @Throws(ZipException::class, IOException::class)
    fun upZipSelectedFile(
        zipFile: File,
        folderPath: String,
        nameContains: String
    ): ArrayList<File> {
        val fileList = ArrayList<File>()
        val desDir = File(folderPath)
        if (!desDir.exists()) {
            desDir.mkdir()
        }
        val zf = ZipFile(zipFile)
        val entries: Enumeration<*> = zf.entries()
        while (entries.hasMoreElements()) {
            val entry = entries.nextElement() as ZipEntry
            if (entry.name.contains(nameContains)) {
                val inputStream = zf.getInputStream(entry)
                var str = folderPath + File.separator + entry.name
                str = String(str.toByteArray(charset("utf-8")), Charset.forName("gbk"))
                val desFile = File(str)
                if (!desFile.exists()) {
                    val fileParentDir = desFile.parentFile
                    if (!fileParentDir.exists()) {
                        fileParentDir.mkdirs()
                    }
                    desFile.createNewFile()
                }
                val out: OutputStream = FileOutputStream(desFile)
                val buffer = ByteArray(BUFF_SIZE)
                var realLength: Int
                while (inputStream.read(buffer).also { realLength = it } > 0) {
                    out.write(buffer, 0, realLength)
                }
                inputStream.close()
                out.close()
                fileList.add(desFile)
            }
        }
        return fileList
    }

    /**
     * 獲得壓縮文件內文件列表
     *
     * @param zipFile 壓縮文件
     * @return 壓縮文件內文件名稱
     * @throws ZipException 壓縮文件格式有誤時拋出
     * @throws IOException  當解壓縮過程出錯時拋出
     */
    @Throws(ZipException::class, IOException::class)
    fun getEntriesNames(zipFile: File?): ArrayList<String> {
        val entryNames = ArrayList<String>()
        val entries =
            getEntriesEnumeration(zipFile)
        while (entries.hasMoreElements()) {
            val entry = entries.nextElement() as ZipEntry
            entryNames.add(
                String(
                    getEntryName(entry).toByteArray(charset("GB2312")),
                    Charset.forName("8859_1")
                )
            )
        }
        return entryNames
    }

    /**
     * 獲得壓縮文件內壓縮文件對像以取得其屬性
     *
     * @param zipFile 壓縮文件
     * @return 返回一個壓縮文件列表
     * @throws ZipException 壓縮文件格式有誤時拋出
     * @throws IOException  IO操作有誤時拋出
     */
    @Throws(ZipException::class, IOException::class)
    fun getEntriesEnumeration(zipFile: File?): Enumeration<*> {
        val zf = ZipFile(zipFile)
        return zf.entries()
    }

    /**
     * 取得壓縮文件對象的名稱
     *
     * @param entry 壓縮文件對象
     * @return 壓縮文件對象的名稱
     * @throws UnsupportedEncodingException
     */
    @Throws(UnsupportedEncodingException::class)
    fun getEntryName(entry: ZipEntry): String {
        return String(entry.name.toByteArray(charset("GB2312")), Charset.forName("8859_1"))
    }

    @Throws(IOException::class)
    fun unzip(zipFileName: String?, outputDirectory: String) {
        var zipFile: ZipFile? = null
        try {
            zipFile = ZipFile(zipFileName)
            val e: Enumeration<*> = zipFile.entries()
            var zipEntry: ZipEntry? = null
            val dest = File(outputDirectory)
            dest.mkdirs()
            while (e.hasMoreElements()) {
                zipEntry = e.nextElement() as ZipEntry
                val entryName = zipEntry.name
                var inputStream: InputStream? = null
                var out: FileOutputStream? = null
                try {
                    if (zipEntry.isDirectory) {
                        var name = zipEntry.name
                        name = name.substring(0, name.length - 1)
                        val f = File(outputDirectory + File.separator + name)
                        f.mkdirs()
                    } else {
                        var index = entryName.lastIndexOf("\\")
                        if (index != -1) {
                            val df = File(
                                outputDirectory + File.separator + entryName.substring(
                                    0,
                                    index
                                )
                            )
                            df.mkdirs()
                        }
                        index = entryName.lastIndexOf("/")
                        if (index != -1) {
                            val df = File(
                                outputDirectory + File.separator
                                        + entryName.substring(0, index)
                            )
                            df.mkdirs()
                        }
                        val f = File(
                            outputDirectory + File.separator
                                    + zipEntry.name
                        )
                        // f.createNewFile();
                        inputStream = zipFile.getInputStream(zipEntry)
                        out = FileOutputStream(f)
                        var c: Int
                        val by = ByteArray(1024)
                        while (inputStream.read(by).also { c = it } != -1) {
                            out.write(by, 0, c)
                        }
                        out.flush()
                    }
                } catch (ex: IOException) {
                    ex.printStackTrace()
                    throw IOException("解壓失敗：$ex")
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close()
                        } catch (ex: IOException) {
                        }
                    }
                    if (out != null) {
                        try {
                            out.close()
                        } catch (ex: IOException) {
                        }
                    }
                }
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            throw IOException("解壓失敗：$ex")
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close()
                } catch (ex: IOException) {
                }
            }
        }
    }
}
