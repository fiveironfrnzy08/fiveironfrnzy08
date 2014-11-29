package org.ryangray.emailsender;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

public class FileChooser extends JPanel implements ActionListener {
	/**
	 * 
	 */
	private static final long	serialVersionUID		= 1L;
	JFileChooser				fc;
	String						callingName;
	List< String >				acceptableExtensions	= new ArrayList< String >( ) {
															private static final long	serialVersionUID	= 1L;
															{
																add( "csv" );
															}
														};

	public FileChooser( JButton openButton ) {
		super( new BorderLayout( ) );

		callingName = openButton.getName( );
		// Create a file chooser
		fc = new JFileChooser( );
		if ( callingName == "toAddressButton" ) {
			fc.setFileSelectionMode( JFileChooser.FILES_ONLY );
			fc.setFileFilter( new FileFilter( ) {

				@Override
				public String getDescription( ) {
					StringBuilder sb = new StringBuilder( );
					sb.append( "Allowed Extensions " );
					for ( String ext: acceptableExtensions ) {
						sb.append( "(*." + ext + ")" );
					}
					return sb.toString( );
				}

				@Override
				public boolean accept( File f ) {
					if ( f.isDirectory( ) ) {
						return true;
					}
					String ext = null;
					String s = f.getName( );
					int i = s.lastIndexOf( '.' );

					if ( i > 0 && i < s.length( ) - 1 ) {
						ext = s.substring( i + 1 ).toLowerCase( );
					}
					if ( acceptableExtensions.contains( ext ) ) {
						return true;
					} else {
						return false;
					}
				}
			} );
		} else if ( callingName == "attachmentsButton" ) {
			fc.setMultiSelectionEnabled( true );
		}

		openButton = new JButton( "Open a File..." );
		openButton.addActionListener( this );

		JPanel buttonPanel = new JPanel( );
		buttonPanel.add( openButton );

		add( buttonPanel, BorderLayout.PAGE_START );
	}

	public void actionPerformed( ActionEvent e ) {

		if ( callingName == "toAddressButton" ) {

			int returnVal = fc.showOpenDialog( FileChooser.this );

			if ( returnVal == JFileChooser.APPROVE_OPTION ) {

				MainWindow.toAddressesFile = fc.getSelectedFile( );
				MainWindow.toAddressFileChosenEdit.setText( MainWindow.toAddressesFile.getName( ) );
				MainWindow.toAddressFileChosenPanel.setVisible( true );

			}

		} else if ( callingName == "attachmentsButton" ) {

			int returnVal = fc.showOpenDialog( FileChooser.this );

			if ( returnVal == JFileChooser.APPROVE_OPTION ) {

				File[] files = fc.getSelectedFiles( );
				MainWindow.attachments = files;
				StringBuilder sb = new StringBuilder( );

				for ( File file: files ) {
					sb.append( file.getName( ) + "; " );
				}

				MainWindow.attachmentsFileListText.setText( sb.toString( ) );
				MainWindow.attachmentsChosenPanel.setVisible( true );

			}
		}

		MainWindow.frmDansEmailDistribution.pack( );

	}

}