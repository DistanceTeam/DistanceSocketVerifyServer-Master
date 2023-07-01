import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

public class AuthServerThread extends Thread {
    DesUtils desUtils = new DesUtils( "TIQSGOD" );

    private Socket socket = null;
    String info = null;

    public AuthServerThread( Socket socket ) {
        this.socket = socket;
    }

    @Override
    public void run( ) {
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        OutputStream os = null;
        PrintWriter pw = null;
        boolean findKey = false;
        boolean findAccount = false;
        try {
            is = socket.getInputStream( );
            isr = new InputStreamReader( is );
            br = new BufferedReader( isr );

            String infomation = null;

            while ( ( infomation = br.readLine( ) ) != null ) {
                info = desUtils.decrypt( infomation );
            }

            System.out.println( "Client Send Pakcet:" + info );
            socket.shutdownInput( );
            os = socket.getOutputStream( );
            pw = new PrintWriter( os );
            if ( info.startsWith( "Reg|" ) ) {//Process Register Packet
                info = info.replace( "Reg|", "" );
                String key = null;
                key = info.split( ":" )[ info.split( ":" ).length - 1 ];
                System.out.printf( "Processing Register Packet\n" );
                //Find key from key file
                Path path = Paths.get( AuthServer.instance.runPath + "/key.txt" );
                List< String > lines = Files.readAllLines( path );

                for ( String keys : lines ) {
                    if ( key.contains( keys ) ) {
                        findKey = true;
                        info = info.replace( ":" + keys, "" );
                        delete( AuthServer.instance.key, keys );
                        System.out.printf( "Delete Key!\n" );
                    }
                }
                if ( findKey ) {
                    findKey = false;
                    pw.write( desUtils.encrypt( "Reg Done." ) );
                    //write user data & remove key
                    BufferedWriter out = new BufferedWriter( new FileWriter( "Account.txt", true ) );
                    out.newLine( );
                    out.write( info );
                    out.close( );
                    System.out.printf( "Created Account! User info:" + info + "\n" );
                } else {
                    pw.write( desUtils.encrypt( "1337." ) );
                }
            }

            if ( info.startsWith( "IRC|" ) ) {

            }

            if ( info.startsWith( "Check|" ) ) {//Process Register Packet
                info = info.replace( "Check|", "" );
                System.out.printf( "Info: " + info );

                //Find key from key file
                List< String > lines = FileManager.read( "Account.txt" );
                for ( String s : lines ) {
                    if ( s.contains( info ) ) {
                        System.out.printf( s );
                        findAccount = true;
                    }
                }
                if ( findAccount ) {
                    pw.write( desUtils.encrypt( "Login Done." ) );
                    pw.flush( );
                    System.out.printf( "\nLogin Done. " + info );
                } else {
                    pw.write( desUtils.encrypt( "Not an account." ) );
                    pw.flush( );
                    System.out.printf( "\nLogin Faild! " + info );
                }
            }
        } catch ( Exception e ) {
            // TODO: handle exception
        } finally {
            //关闭资源
            //if (info.contains("ReadDone")) {
            //Delay
            if ( new TimerUtil( ).delay( 400 ) ) {
                try {
                    if ( pw != null )
                        pw.close( );
                    if ( os != null )
                        os.close( );
                    if ( br != null )
                        br.close( );
                    if ( isr != null )
                        isr.close( );
                    if ( is != null )
                        is.close( );
                    if ( socket != null )
                        socket.close( );
                } catch ( IOException e ) {
                    e.printStackTrace( );
                }
                //}
            }
        }
    }

    public static void delete( String file, String text ) {
        delete( new File( file ), text );
    }

    public static void delete( File file, String text ) {
        File temp = null;
        BufferedReader br = null;
        PrintWriter pw = null;
        try {
            temp = File.createTempFile( "temp", "temp" );
            pw = new PrintWriter( temp );
            br = new BufferedReader( new FileReader( file ) );
            while ( br.ready( ) ) {
                String line = br.readLine( );
                System.out.println( line );
                if ( line.equals( text ) ) {
                    continue;
                }
                pw.println( line );
            }
            pw.flush( );
        } catch ( IOException e ) {
            e.printStackTrace( );
        } finally {
            safeClose( br );
            safeClose( pw );
            if ( temp != null ) {
                file.delete( );
                temp.renameTo( file );
            }
        }
    }

    private static void safeClose( Closeable closeable ) {
        if ( closeable != null ) {
            try {
                closeable.close( );
            } catch ( IOException e ) {
                e.printStackTrace( );
            }
        }
    }
}