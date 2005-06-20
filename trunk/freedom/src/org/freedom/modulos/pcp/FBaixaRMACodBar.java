/**
 * @version 20/06/2005 <BR>
 * @author Setpoint Inform�tica Ltda./Anderson Sanchez <BR>
 * 
 * Projeto: Freedom <BR>
 *  
 * Pacote: org.freedom.modulos.pcp <BR>
 * Classe:
 * @(#)FreedomPCP.java <BR>
 * 
 * Este programa � licenciado de acordo com a LPG-PC (Licen�a P�blica Geral para
 * Programas de Computador), <BR>
 * vers�o 2.1.0 ou qualquer vers�o posterior. <BR>
 * A LPG-PC deve acompanhar todas PUBLICA��ES, DISTRIBUI��ES e REPRODU��ES deste
 * Programa. <BR>
 * Caso uma c�pia da LPG-PC n�o esteja dispon�vel junto com este Programa, voc�
 * pode contatar <BR>
 * o LICENCIADOR ou ent�o pegar uma c�pia em: <BR>
 * Licen�a: http://www.lpg.adv.br/licencas/lpgpc.rtf <BR>
 * Para poder USAR, PUBLICAR, DISTRIBUIR, REPRODUZIR ou ALTERAR este Programa �
 * preciso estar <BR>
 * de acordo com os termos da LPG-PC <BR>
 * <BR>
 * 
 * Tela de baixa de RMA por C�digo de barras.
 *  
 */
package org.freedom.modulos.pcp;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;

import javax.swing.JTextField;

import org.freedom.acao.CarregaEvent;
import org.freedom.acao.CarregaListener;
import org.freedom.componentes.JTextFieldPad;
import org.freedom.funcoes.Funcoes;
import org.freedom.telas.FDados;

public class FBaixaRMACodBar extends FDados implements CarregaListener,FocusListener,KeyListener{
  private JTextField txtEntrada = new JTextField();
  private JTextFieldPad txtSeqOf = new JTextFieldPad(JTextFieldPad.TP_INTEGER,8,0);
  private JTextFieldPad txtCodOp = new JTextFieldPad(JTextFieldPad.TP_INTEGER,8,0);
  private JTextFieldPad txtCodProd = new JTextFieldPad(JTextFieldPad.TP_INTEGER,8,0);
  private JTextFieldPad txtCodLote = new JTextFieldPad(JTextFieldPad.TP_STRING,8,0);
  private JTextFieldPad txtQtdEntrada = new JTextFieldPad(JTextFieldPad.TP_DECIMAL,15,5);
    
  public FBaixaRMACodBar () {
    setTitulo("Cadastro de Tipo de Fornecedor");
    setAtribos( 50, 50, 350, 350);
    
    adic(txtEntrada,7,10,150,20);
    adic(txtSeqOf,7,30,150,20);
    adic(txtCodOp,7,70,150,20);
    adic(txtCodProd,7,110,150,20);
    adic(txtCodLote,7,150,150,20);
    adic(txtQtdEntrada,7,190,150,20);
    
    txtEntrada.addFocusListener(this);
    btImp.addActionListener(this);
    btPrevimp.addActionListener(this);     
    txtEntrada.addKeyListener(this);
  }
  public void beforeCarrega(CarregaEvent cevt){  }
  public void afterCarrega(CarregaEvent cevt){  }
  public void focusGained(FocusEvent e) {  }
  public void focusLost(FocusEvent e) { 
  	if(e.getSource()==txtEntrada){
  		decodeEntrada();
  	}
  }
  public void keyPressed(KeyEvent kevt) {
  	if(kevt.getSource()==txtEntrada){
  		System.out.println("COD: "+kevt.getKeyCode());
  		System.out.println("CHR: "+kevt.getKeyChar());
  	}
  }
  public void keyTyped(KeyEvent kevt) {
//  	if(kevt.getSource()==txtEntrada)
  //		System.out.println("KT"+kevt.getKeyCode());
  
  }
  private void decodeEntrada(){
  	String sTexto = txtEntrada.getText();
  	if(sTexto!=null){
		if (sTexto.length()>0){
			int iCampos = Funcoes.contaChar(sTexto,'#'); 
			if(iCampos==4) {
				Vector vCampos = new Vector();
				vCampos.addElement(txtSeqOf);
				vCampos.addElement(txtCodOp);
				vCampos.addElement(txtCodProd);
				vCampos.addElement(txtCodLote); 
				vCampos.addElement(txtQtdEntrada);

				String sResto = sTexto;
				
				for(int i=0;iCampos>i;i++){					
					((JTextFieldPad)(vCampos.elementAt(i))).setVlrString(sResto.substring(0,sResto.indexOf("#")));
					sResto = sResto.substring(sResto.indexOf("#")+1);
				}
			}
			else{
				Funcoes.mensagemInforma(this,"Entrada inv�lida!\nN�mero de campos incoerente."+Funcoes.contaChar(sTexto,'#'));
			}
		}
		else {
			Funcoes.mensagemInforma(this,"Entrada inv�lida!\nTexto em branco.");
		}
  	}
  	else{
  		Funcoes.mensagemInforma(this,"Entrada inv�lida!\nTexto nulo.");
  	}
  }
	
}
