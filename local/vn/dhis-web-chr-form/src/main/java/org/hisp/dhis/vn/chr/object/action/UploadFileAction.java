package org.hisp.dhis.vn.chr.object.action;

/**
 * @author Chau Thu Tran
 * 
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.options.SystemSettingManager;

import com.opensymphony.xwork2.Action;

public class UploadFileAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SystemSettingManager systemSettingManager;

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    // -----------------------------------------------------------------------------------------------
    // Input && Output
    // -----------------------------------------------------------------------------------------------

    private List<File> files;

    public List<File> getFiles()
    {
        return files;
    }

    private File uploadFile;

    public void setUploadFile( File uploadFile )
    {
        this.uploadFile = uploadFile;
    }

    private String uploadFilename;

    public void setUploadFilename( String uploadFilename )
    {
        this.uploadFilename = uploadFilename;
    }

    // -----------------------------------------------------------------------------------------------
    // Implements
    // -----------------------------------------------------------------------------------------------

    public String execute()
    {
        files = new ArrayList<File>();

        FileInputStream fin = null;
        FileOutputStream fout = null;

        String imageDirectoryOnServer = (String) systemSettingManager
            .getSystemSetting( SystemSettingManager.KEY_CHR_IMAGE_DIRECTORY );

        try
        {

            if ( uploadFile != null )
            {
                fin = new FileInputStream( uploadFile );// (doc.getPath());
                byte[] data = new byte[8192];
                int byteReads = fin.read( data );

                fout = new FileOutputStream( imageDirectoryOnServer + "/" + uploadFilename );

                while ( byteReads != -1 )
                {
                    fout.write( data, 0, byteReads );
                    fout.flush();
                    byteReads = fin.read( data );
                }
                fin.close();
                fout.close();
            }

            // Load files in the directory on server
            File myDir = new File( imageDirectoryOnServer );
            if ( myDir.exists() && myDir.isDirectory() )
            {
                File[] listfiles = myDir.listFiles();
                for ( int i = 0; i < listfiles.length; i++ )
                {
                    if ( !listfiles[i].isDirectory() && !listfiles[i].isHidden() )
                        files.add( listfiles[i] );
                }
            }

            return SUCCESS;

        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
            return ERROR;
        }

    }

}