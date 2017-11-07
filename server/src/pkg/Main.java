package pkg;

import pkg.Server;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;

import java.io.IOException;

import javax.swing.JOptionPane;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class Main {

	protected Shell shell;
	private Text ParentIP_TextField;
	private Text ParentPort_TextField;
	private Text DupIP_TextField;
	private Text DupPort_TextField;
	private Text ServerPort_TextField;
	
	private Server Server;
	
	private String Parent = new String();
	private String DupParent = new String();


	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Main window = new Main();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("Server Initiation");
		shell.setLayout(new GridLayout(5, false));
		
		Label who_is_lbl = new Label(shell, SWT.NONE);
		who_is_lbl.setText("Parent server:");
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		
		Label ParentIP_lbl = new Label(shell, SWT.NONE);
		ParentIP_lbl.setText("Parent IP");
		
		Label ParentPort_lbl = new Label(shell, SWT.NONE);
		ParentPort_lbl.setText("Parent Port");
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		
		ParentIP_TextField = new Text(shell, SWT.BORDER);
		//ParentIP_TextField.setText("Enter Parent IP Here");
		ParentIP_TextField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		ParentPort_TextField = new Text(shell, SWT.BORDER);
		//ParentPort_TextField.setText("Enter Parent Port Here");
		ParentPort_TextField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		
		Button Set_btn = new Button(shell, SWT.NONE);
		Set_btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				//add set action, at the end, button should be invisible so user can only add one parent
				Parent = ParentIP_TextField.getText() + ":" + ParentPort_TextField.getText();
				if (Parent.equalsIgnoreCase(":")){
					//System.out.println("NO PARENT");
					JOptionPane.showMessageDialog(null, "Please Enter Parent's Information");
					}
				else{
					//System.out.println("update_server parent "+ParentIP_TextField.getText()+" true");
					try {
						Process process = Runtime.getRuntime().exec("update_server parent "+ParentIP_TextField.getText()+" true");
						System.out.println("Here in Process");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					Set_btn.setVisible(false);
				}
			}
		});
		Set_btn.setText("SET");
		new Label(shell, SWT.NONE);
		
		Label children_lbl = new Label(shell, SWT.NONE);
		children_lbl.setText("Parent Backup Server:");
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		
		Label ChildIP_lbl = new Label(shell, SWT.NONE);
		ChildIP_lbl.setText("BackUp IP");
		
		Label ChildPort_lbl = new Label(shell, SWT.NONE);
		ChildPort_lbl.setText("BackUp Port");
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		
		DupIP_TextField = new Text(shell, SWT.BORDER);
		//ChildIP_TextField.setText("Enter Child IP Here");
		DupIP_TextField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		DupPort_TextField = new Text(shell, SWT.BORDER);
		//ChildPort_TextField.setText("Enter Child Port Here");
		DupPort_TextField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label_1 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		
		Button Add_BU_btn = new Button(shell, SWT.NONE);
		Add_BU_btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//Add actions...
				DupParent = DupIP_TextField.getText() + ":" + DupPort_TextField.getText();
				if (DupParent.equalsIgnoreCase(":")){
					//System.out.println("NO PARENT");
					JOptionPane.showMessageDialog(null, "Please Enter BackUp Parent's Information");
					}
				else if(DupParent.equals(ParentIP_TextField.getText()+":"+ParentPort_TextField.getText())){
					JOptionPane.showMessageDialog(null, "Parent and BackUp Parent cannot be the same");
				}
				else{
					//System.out.println(Parent);
					Add_BU_btn.setVisible(false);
					//System.out.println("update_server parent "+DupIP_TextField.getText()+" false");
					try {
						Runtime.getRuntime().exec("update_server parent "+DupIP_TextField.getText()+" false");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}	
				
			}
		});
		Add_BU_btn.setText("SET");
		new Label(shell, SWT.NONE);
		
		Label PortNom_lbl = new Label(shell, SWT.NONE);
		PortNom_lbl.setText("Port Number");
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		
		Button btnCheckButton = new Button(shell, SWT.CHECK);
		btnCheckButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(btnCheckButton.getSelection()){
					//System.out.println("check box enabled");
					ServerPort_TextField.setText("5060");
					ServerPort_TextField.setVisible(false);
				}
				else{
					ServerPort_TextField.setVisible(true);
				}
					
			}
		});
		btnCheckButton.setText("Using 5060 as the defaul port");
		btnCheckButton.setSelection(true);
		
		ServerPort_TextField = new Text(shell, SWT.BORDER);
		ServerPort_TextField.setVisible(false);
		ServerPort_TextField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		
		Button Done_btn = new Button(shell, SWT.NONE);
		Done_btn.setText("Done");
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		
		Done_btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//Done, close the window open the server Window ;)
				try {
					if (!Parent.isEmpty() && !DupParent.isEmpty()){
						Server = new Server();
						shell.setVisible(false);
						}
					else{
						JOptionPane.showMessageDialog(null, "Please Enter full information needed");
					}
					
				} catch (Exception e2) {
					// TODO: handle exception
					System.out.println(e2.getMessage());
				}
				
								
			}
		});


	}
}
