import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class RSVP {
	
	public static void main( String[] args ) throws IOException {
		
		File[] files = new File[] { new File( "C:\\users\\rgray\\Desktop\\Wedding.csv" ), new File( "C:\\users\\rgray\\Desktop\\Wedding (2).csv" ) };
		FileWriter fw = new FileWriter( new File( "C:\\users\\rgray\\Desktop\\RSVP.csv" ) );
		
		for ( File file: files ) {
			BufferedReader br = new BufferedReader( new FileReader( file ) );
			
			List< List< String > > groups = new ArrayList<>();
			
			String line;
			while ( ( line = br.readLine( ) ) != null ) {
				
				if ( line.contains( "-" ) ) {
					continue;
				}
				
				String[] tokens = line.split( "[,&]" );
				String lastName = "";
				
				for ( String token: tokens ) {
					token = token.trim( );
					System.out.println( token );
					if ( token.contains( " " ) ) {
						if ( "".equals( lastName ) ) {
							String[] nameTokens = token.split( " " );
							lastName = nameTokens[ nameTokens.length - 1 ];
						}
					}
				}
				
				List< String > group = new ArrayList<>( );
				for ( String name: tokens ) {
					if ( "".equals( name.trim( ) ) ) {
						continue;
					}
					if ( ( name.toUpperCase( ).contains( "Guest".toUpperCase( ) ) || name.toUpperCase( ).contains( "Family".toUpperCase( ) ) ) ) {
						continue;
					}
					if ( name.trim( ).contains( " " ) ) {
						group.add( name.trim( ) );
					} else {
						group.add( name.trim( ) + " " + lastName );
					}
				}
				groups.add( group );
				
			}
			
			br.close( );

			for ( List< String > list: groups ) {
				StringBuffer sb = new StringBuffer( );
				for ( int i = 0; i < list.size( ); i++ ) {
					System.out.println( list.get( i ) );
					if ( list.size( ) > 1 ) {
						if ( i == 0 ) {
							String[] firstLast = list.get( i ).split( " " );
							if ( firstLast.length == 1 ) {
								sb.append( list.get( i ) + ",,,\"" );
							} else if ( firstLast.length == 2 ) {
								sb.append( firstLast[0] + "," + firstLast[1] + ",,\"" );
							} else if ( firstLast.length == 3 ) {
								sb.append( firstLast[0] + " " + firstLast[1] + "," + firstLast[2] + ",,,\"" );
							}
						} else if ( i == ( list.size( ) - 1 ) ) {
							sb.append( list.get( i ) + "\"");
						} else {
							sb.append( list.get( i ) + "," );
						}
					} else {
						String[] firstLast = list.get( i ).split( " " );
						if ( firstLast.length == 1 ) {
							sb.append( list.get( i ) );
						} else if ( firstLast.length == 2 ) {
							sb.append( firstLast[0] + "," + firstLast[1] );
						} else if ( firstLast.length == 3 ) {
							sb.append( firstLast[0] + " " + firstLast[1] + "," + firstLast[2] );
						}
					}
					
				}
				fw.append( sb.toString( ) + "\r\n" );
			}
		}
		
		fw.flush( );
		fw.close( );
		
	}

}
