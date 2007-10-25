/**
 * @version 01/03/2007 <BR>
 * @author Setpoint Inform�tica Ltda./RobsonSanchez/Alex Rodrigues<BR>
 * 
 * Projeto: Freedom <BR>
 * 
 * Pacote: org.freedom.modulos.std <BR>
 * Classe:
 * @(#)FRemFBN.java <BR>
 * 
 * Este programa � licenciado de acordo com a LPG-PC (Licen�a P�blica Geral para Programas de Computador), <BR>
 * vers�o 2.1.0 ou qualquer vers�o posterior. <BR>
 * A LPG-PC deve acompanhar todas PUBLICA��ES, DISTRIBUI��ES e REPRODU��ES deste Programa. <BR>
 * Caso uma c�pia da LPG-PC n�o esteja dispon�vel junto com este Programa, voc� pode contatar <BR>
 * o LICENCIADOR ou ent�o pegar uma c�pia em: <BR>
 * Licen�a: http://www.lpg.adv.br/licencas/lpgpc.rtf <BR>
 * Para poder USAR, PUBLICAR, DISTRIBUIR, REPRODUZIR ou ALTERAR este Programa � preciso estar <BR>
 * de acordo com os termos da LPG-PC <BR>
 * <BR>
 * 
 * Tela de remessa de arquivo, contendo os dados dos clientes e recebimentos, para o banco selecionado.
 * 
 */

package org.freedom.modulos.fnc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import org.freedom.acao.RadioGroupEvent;
import org.freedom.acao.RadioGroupListener;
import org.freedom.bmps.Icone;
import org.freedom.componentes.GuardaCampo;
import org.freedom.componentes.JPanelPad;
import org.freedom.componentes.JRadioGroup;
import org.freedom.componentes.JTextFieldFK;
import org.freedom.componentes.JTextFieldPad;
import org.freedom.componentes.ListaCampos;
import org.freedom.componentes.Tabela;
import org.freedom.funcoes.Boleto;
import org.freedom.funcoes.Funcoes;
import org.freedom.modulos.fnc.FbnUtil.EPrefs;
import org.freedom.telas.Aplicativo;
import org.freedom.telas.FFilho;

public abstract class FRemFBN extends FFilho implements ActionListener, MouseListener, RadioGroupListener {

	private static final long serialVersionUID = 1L;
	
	protected static final String TIPO_FEBRABAN_SIACC = "01";
	
	protected static final String TIPO_FEBRABAN_CNAB = "02";
	
	protected final String TIPO_FEBRABAN;

	private JPanelPad panelRodape = null;

	private final JPanelPad panelRemessa = new JPanelPad( JPanelPad.TP_JPANEL, new BorderLayout() );

	private final JPanelPad panelFiltros = new JPanelPad();

	private final JPanelPad panelTabela = new JPanelPad( JPanelPad.TP_JPANEL, new BorderLayout() );

	private final JPanelPad panelFuncoes = new JPanelPad();

	private final JPanelPad panelStatus = new JPanelPad( JPanelPad.TP_JPANEL, new BorderLayout() );
	
	private final JPanelPad panelImp = new JPanelPad( JPanelPad.TP_JPANEL, new FlowLayout( FlowLayout.CENTER, 0, 0 ) );

	private final JPanelPad pnImp = new JPanelPad( JPanelPad.TP_JPANEL, new GridLayout( 1, 2 ) );

	protected final Tabela tab = new Tabela();

	protected final JTextFieldPad txtCodBanco = new JTextFieldPad( JTextFieldPad.TP_STRING, 3, 0 );

	protected final JTextFieldFK txtNomeBanco = new JTextFieldFK( JTextFieldPad.TP_STRING, 50, 0 );

	protected final JTextFieldPad txtDtIni = new JTextFieldPad( JTextFieldPad.TP_DATE, 10, 0 );

	protected final JTextFieldPad txtDtFim = new JTextFieldPad( JTextFieldPad.TP_DATE, 10, 0 );

	protected JRadioGroup<String, String> rgData;
	
	protected JRadioGroup<String, String> rgSitRemessa;
	
	protected JRadioGroup<String, String> rgTipoRemessa;

	private final JButton btCarrega = new JButton( "Buscar", Icone.novo( "btExecuta.gif" ) );

	private final JButton btExporta = new JButton( "Exportar", Icone.novo( "btSalvar.gif" ) );

	private final JButton btSelTudo = new JButton( Icone.novo( "btTudo.gif" ) );

	private final JButton btSelNada = new JButton( Icone.novo( "btNada.gif" ) );
	
	private final JButton btImprime = new JButton( Icone.novo( "btImprime.gif" ) );
	
	private final JButton btVisImp = new JButton( Icone.novo( "btPrevimp.gif" ) );

	protected final JLabel lbStatus = new JLabel();

	protected final ListaCampos lcBanco = new ListaCampos( this );

	protected Map<Enum<EPrefs>, Object> prefs = new HashMap<Enum<EPrefs>, Object>();
	
	protected String where = "";
	
	
	public FRemFBN( final String tipofebraban ) {

		super( false );
		setTitulo( "Manuten��o de contas a receber" );
		setAtribos( 10, 10, 780, 540 );
		
		this.TIPO_FEBRABAN = tipofebraban;
		
		montaRadioGrupos();
		montaListaCampos();
		montaTela();

		tab.adicColuna( "Sel." );
		tab.adicColuna( "Raz�o social do cliente" );
		tab.adicColuna( "C�d.cli." );
		tab.adicColuna( "C�d.rec." );
		tab.adicColuna( "Doc" );
		tab.adicColuna( "Nro.Parc." );
		tab.adicColuna( "Valor" );
		tab.adicColuna( "Emiss�o" );
		tab.adicColuna( "Vencimento" );
		tab.adicColuna( "Ag�ncia" );
		tab.adicColuna( "Indentifica��o" );
		tab.adicColuna( "Sit. rem." );
		tab.adicColuna( "Sit. ret." );
		tab.adicColuna( "Subtipo" );
		tab.adicColuna( "Tp.r.cli." );
		tab.adicColuna( "Pessoa" );
		tab.adicColuna( "C.P.F." );
		tab.adicColuna( "C.N.P.J." );
		tab.adicColuna( "Cart. cob." );

		tab.setTamColuna( 20, EColTab.COL_SEL.ordinal() );
		tab.setTamColuna( 150, EColTab.COL_RAZCLI.ordinal() );
		tab.setTamColuna( 70, EColTab.COL_CODCLI.ordinal() );
		tab.setTamColuna( 70, EColTab.COL_CODREC.ordinal() );
		tab.setTamColuna( 80, EColTab.COL_DOCREC.ordinal() );
		tab.setTamColuna( 70, EColTab.COL_NRPARC.ordinal() );
		tab.setTamColuna( 70, EColTab.COL_VLRAPAG.ordinal() );
		tab.setTamColuna( 70, EColTab.COL_DTREC.ordinal() );
		tab.setTamColuna( 70, EColTab.COL_DTVENC.ordinal() );
		tab.setTamColuna( 100, EColTab.COL_AGENCIACLI.ordinal() );
		tab.setTamColuna( 100, EColTab.COL_IDENTCLI.ordinal() );
		tab.setTamColuna( 50, EColTab.COL_SITREM.ordinal() );
		tab.setTamColuna( 50, EColTab.COL_SITRET.ordinal() );
		tab.setTamColuna( 30, EColTab.COL_STIPOFEBRABAN.ordinal() );
		tab.setTamColuna( 30, EColTab.COL_TIPOREMCLI.ordinal() );
		tab.setTamColuna( 30, EColTab.COL_PESSOACLI.ordinal() );
		tab.setTamColuna( 80, EColTab.COL_CPFCLI.ordinal() );
		tab.setTamColuna( 80, EColTab.COL_CNPJCLI.ordinal() );
		tab.setTamColuna( 80, EColTab.COL_CARTEIRA.ordinal() );
		tab.setColunaEditavel( EColTab.COL_SEL.ordinal(), true );
		tab.addMouseListener( this );

		btCarrega.addActionListener( this );
		btSelTudo.addActionListener( this );
		btSelNada.addActionListener( this );
		btExporta.addActionListener( this );
		btImprime.addActionListener( this );
		btVisImp.addActionListener( this );		
		
		rgTipoRemessa.addRadioGroupListener( this );
		
		btSelTudo.setToolTipText( "Selecionar tudo" );
		btSelNada.setToolTipText( "Limpar sele��o" );

		Calendar cal = Calendar.getInstance();
		txtDtFim.setVlrDate( cal.getTime() );
		cal.set( Calendar.MONTH, cal.get( Calendar.MONTH )-1 );
		txtDtIni.setVlrDate( cal.getTime() );

	}
	
	private void montaRadioGrupos() {
		
		Vector<String> vValsDate = new Vector<String>();
		
		Vector<String> vLabsDate = new Vector<String>();

		Vector<String> vValsRem = new Vector<String>();
		
		Vector<String> vLabsRem = new Vector<String>();

		Vector<String> vValsTipo = new Vector<String>();
		
		Vector<String> vLabsTipo = new Vector<String>();

		vValsDate.addElement( "E" );
		vValsDate.addElement( "V" );
		vLabsDate.addElement( "Emiss�o" );
		vLabsDate.addElement( "Vencimento" );
		rgData = new JRadioGroup<String, String>( 2, 1, vLabsDate, vValsDate );
		
		vValsRem.addElement( "00" );
		vValsRem.addElement( "01" );
		vValsRem.addElement( "02" );
		vValsRem.addElement( "99" );
		vLabsRem.addElement( "N�o exportados" );
		vLabsRem.addElement( "Exportados" );
		vLabsRem.addElement( "Rejeitados" );
		vLabsRem.addElement( "Todos" );
		
		rgSitRemessa = new JRadioGroup<String, String>( 2, 2, vLabsRem, vValsRem );
		
		vValsTipo.addElement( "0" );
		vValsTipo.addElement( "1" );
		vLabsTipo.addElement( "Inclus�o" );
		vLabsTipo.addElement( "Exclus�o" );
		rgTipoRemessa = new JRadioGroup<String, String>( 2, 1, vLabsTipo, vValsTipo );
	}
	
	private void montaListaCampos() {
		
		/***************
		 *   FNBANCO   *
		 ***************/

		lcBanco.add( new GuardaCampo( txtCodBanco, "CodBanco", "C�d.banco", ListaCampos.DB_PK, true ) );
		lcBanco.add( new GuardaCampo( txtNomeBanco, "NomeBanco", "Nome do Banco", ListaCampos.DB_SI, false ) );
		lcBanco.montaSql( false, "BANCO", "FN" );
		lcBanco.setQueryCommit( false );
		lcBanco.setReadOnly( true );
		txtCodBanco.setNomeCampo( "CodBanco" );
		txtCodBanco.setTabelaExterna( lcBanco );
		txtCodBanco.setListaCampos( lcBanco );
		txtCodBanco.setFK( true );
		txtCodBanco.setRequerido( true );
		txtNomeBanco.setListaCampos( lcBanco );
	}

	private void montaTela() {
	
		pnCliente.add( panelRemessa, BorderLayout.CENTER );
	
		panelRemessa.add( panelFiltros, BorderLayout.NORTH );
		panelRemessa.add( panelTabela, BorderLayout.CENTER );
		panelRemessa.add( panelStatus, BorderLayout.SOUTH );

		JLabel bordaData = new JLabel();
		bordaData.setBorder( BorderFactory.createEtchedBorder() );
		JLabel periodo = new JLabel( "Periodo", SwingConstants.CENTER );
		periodo.setOpaque( true );
		
		panelFiltros.setPreferredSize( new Dimension( 300, 165 ) );
		panelFiltros.adic( new JLabel( "C�d.banco" ), 7, 10, 90, 20 );
		panelFiltros.adic( txtCodBanco, 7, 30, 90, 20 );
		panelFiltros.adic( new JLabel( "Nome do banco" ), 100, 10, 300, 20 );
		panelFiltros.adic( txtNomeBanco, 100, 30, 318, 20 );	
		
		panelFiltros.adic( periodo, 443, 10, 80, 20 );
		panelFiltros.adic( txtDtIni, 445, 30, 120, 20 );
		panelFiltros.adic( new JLabel( "at�", SwingConstants.CENTER ), 565, 30, 50, 20 );
		panelFiltros.adic( txtDtFim, 615, 30, 120, 20 );
		panelFiltros.adic( bordaData, 433, 20, 317, 40 );	

		panelFiltros.adic( new JLabel( "Tipo de remessa:" ), 7, 60, 150, 20 );
		panelFiltros.adic( rgTipoRemessa, 7, 80, 150, 70 );
		panelFiltros.adic( new JLabel( "filtro:" ), 170, 60, 250, 20 );
		panelFiltros.adic( rgSitRemessa, 170, 80, 250, 70 );
		panelFiltros.adic( new JLabel( "filtro:" ), 433, 60, 150, 20 );
		panelFiltros.adic( rgData, 433, 80, 150, 70 );
	
		panelFiltros.adic( btCarrega, 600, 100, 150, 30 );
	
		panelTabela.add( new JScrollPane( tab ), BorderLayout.CENTER );
		panelTabela.add( panelFuncoes, BorderLayout.EAST );
	
		panelFuncoes.setPreferredSize( new Dimension( 45, 100 ) );
		panelFuncoes.adic( btSelTudo, 5, 5, 30, 30 );
		panelFuncoes.adic( btSelNada, 5, 40, 30, 30 );
		
		lbStatus.setForeground( Color.BLUE );
	
		panelStatus.setPreferredSize( new Dimension( 600, 30 ) );
		panelStatus.add( lbStatus, BorderLayout.WEST );
	
		panelRodape = adicBotaoSair();
		panelRodape.setBorder( BorderFactory.createEtchedBorder() );
		panelRodape.setPreferredSize( new Dimension( 600, 32 ) );
		btExporta.setPreferredSize( new Dimension( 150, 30 ) );
		panelRodape.add( btExporta, BorderLayout.WEST );
		
		panelRodape.add( panelImp, BorderLayout.CENTER );
		panelImp.add( pnImp, BorderLayout.NORTH );
		pnImp.setPreferredSize( new Dimension( 60, 30 ) );
		pnImp.add( btImprime );
		pnImp.add( btVisImp );
		
	}

	protected String getMenssagemRet( final String codretorno ) {
		
		String msg = null; 
		StringBuilder sSQL = new StringBuilder();
		PreparedStatement ps = null;
		
		try {
			
			sSQL.append( " SELECT DESCRET " );
			sSQL.append( " FROM FNFBNCODRET " );
			sSQL.append( " WHERE CODEMP=? AND CODFILIAL=?  AND CODEMPBO=? " );
			sSQL.append( " AND CODFILIALBO=?  AND CODRET=? AND TIPOFEBRABAN=? "  );
			
			ps = con.prepareStatement( sSQL.toString() );
			
			ps.setInt( 1,  Aplicativo.iCodEmp );
			ps.setInt( 2, ListaCampos.getMasterFilial( "VDCLIENTE" ) );
			ps.setInt( 3, Aplicativo.iCodEmp );
			ps.setInt( 4, ListaCampos.getMasterFilial( "FNBANCO" ) );
			ps.setString( 5, codretorno );
			ps.setString( 6, TIPO_FEBRABAN );
			
			ResultSet rs = ps.executeQuery();
			
			if( rs.next() ){
				
				msg = rs.getString( "DESCRET" );
			}
		} catch ( Exception e ) {
			Funcoes.mensagemInforma( this, "Erro ao montar grid. \n" + e.getMessage() );
			e.printStackTrace();
		}
		return msg;		
	}

	protected boolean setPrefs() {

		boolean retorno = false;

		try {
			
			StringBuilder sql = new StringBuilder();
			
			sql.append( "SELECT I.CODCONV, P.NOMEEMP, I.VERLAYOUT, I.IDENTSERV, I.CONTACOMPR, " );
			sql.append( "I.IDENTAMBCLI, I.IDENTAMBBCO, I.NROSEQ, " );
			sql.append( "I.NUMCONTA, C.AGENCIACONTA, E.CNPJFILIAL, " );
			sql.append( "FORCADTIT, TIPODOC, IDENTEMITBOL , IDENTDISTBOL, ESPECTIT, CODJUROS, VLRPERCJUROS, " );
			sql.append( "CODDESC, VLRPERCDESC, CODPROT, DIASPROT, CODBAIXADEV, DIASBAIXADEV, I.MDECOB, I.CONVCOB, I.ACEITE " );
			sql.append( "FROM SGPREFERE6 P, SGFILIAL E, " );			
			sql.append( "SGITPREFERE6 I LEFT OUTER JOIN FNCONTA C ON " );
			sql.append( "C.CODEMP=I.CODEMPCA AND C.CODFILIAL=I.CODFILIALCA AND C.NUMCONTA=I.NUMCONTA " );			
			sql.append( "WHERE I.CODEMP=? AND I.CODFILIAL=? " );
			sql.append( "AND I.CODEMPBO=? AND I.CODFILIALBO=? AND I.CODBANCO=? AND I.TIPOFEBRABAN=? " );
			sql.append( "AND P.CODEMP=I.CODEMP AND P.CODFILIAL=I.CODFILIAL " );
			sql.append( "AND E.CODEMP=I.CODEMP AND E.CODFILIAL=I.CODFILIAL " );

			PreparedStatement ps = con.prepareStatement( sql.toString() );
			ps.setInt( 1, Aplicativo.iCodEmp );
			ps.setInt( 2, ListaCampos.getMasterFilial( "SGITPREFERE6" ) );
			ps.setInt( 3, Aplicativo.iCodEmp );
			ps.setInt( 4, ListaCampos.getMasterFilial( "FNBANCO" ) );
			ps.setString( 5, txtCodBanco.getVlrString() );
			ps.setString( 6, TIPO_FEBRABAN );
			
			ResultSet rs = ps.executeQuery();
			
			if ( rs.next() ) {
				
				prefs.put( EPrefs.CODCONV, rs.getString( EPrefs.CODCONV.toString() ) );
				prefs.put( EPrefs.NOMEEMP, rs.getString( EPrefs.NOMEEMP.toString() ) );
				prefs.put( EPrefs.VERLAYOUT, rs.getString( EPrefs.VERLAYOUT.toString() ) );
				prefs.put( EPrefs.CODBANCO, txtCodBanco.getVlrString() );
				prefs.put( EPrefs.NOMEBANCO, txtNomeBanco.getVlrString() );
				prefs.put( EPrefs.IDENTSERV, rs.getString( EPrefs.IDENTSERV.toString() ) );
				prefs.put( EPrefs.CONTACOMPR, rs.getString( EPrefs.CONTACOMPR.toString() ) );
				prefs.put( EPrefs.IDENTAMBCLI, rs.getString( EPrefs.IDENTAMBCLI.toString() ) );
				prefs.put( EPrefs.IDENTAMBBCO, rs.getString( EPrefs.IDENTAMBBCO.toString() ) );
				prefs.put( EPrefs.NROSEQ, new Integer( rs.getInt( EPrefs.NROSEQ.toString() ) ) );

				if ( rs.getString( "AGENCIACONTA" ) != null ) {
					String[] agencia = Boleto.getCodSig( rs.getString( "AGENCIACONTA" ) );
					prefs.put( EPrefs.AGENCIA, agencia[ 0 ] );
					prefs.put( EPrefs.DIGAGENCIA, agencia[ 1 ] );
				}
				else {
					prefs.put( EPrefs.AGENCIA, "" );
					prefs.put( EPrefs.DIGAGENCIA, "" );
				}
				
				if ( rs.getString( EPrefs.NUMCONTA.toString() ) != null ) {
					String[] conta = Boleto.getCodSig( rs.getString( EPrefs.NUMCONTA.toString() ) );
					prefs.put( EPrefs.NUMCONTA, conta[ 0 ] );
					prefs.put( EPrefs.DIGCONTA, conta[ 1 ] );
				}
				else {
					prefs.put( EPrefs.NUMCONTA, "" );
					prefs.put( EPrefs.DIGCONTA, "" );
				}
				
				prefs.put( EPrefs.DIGAGCONTA, null );
				prefs.put( EPrefs.CNPFEMP, rs.getString( "CNPJFILIAL" ) );
				
				prefs.put( EPrefs.FORCADTIT, rs.getInt( EPrefs.FORCADTIT.toString() ) );
				prefs.put( EPrefs.TIPODOC, rs.getInt( EPrefs.TIPODOC.toString() ) );
				prefs.put( EPrefs.IDENTEMITBOL, rs.getInt( EPrefs.IDENTEMITBOL.toString() ) );
				prefs.put( EPrefs.IDENTDISTBOL, rs.getInt( EPrefs.IDENTDISTBOL.toString() ) );
				prefs.put( EPrefs.ESPECTIT, rs.getInt( EPrefs.ESPECTIT.toString() ) );
				prefs.put( EPrefs.CODJUROS, rs.getInt( EPrefs.CODJUROS.toString() ) );
				prefs.put( EPrefs.VLRPERCJUROS, rs.getBigDecimal( EPrefs.VLRPERCJUROS.toString() ) );
				prefs.put( EPrefs.CODDESC, rs.getInt( EPrefs.CODDESC.toString() ) );
				prefs.put( EPrefs.VLRPERCDESC, rs.getBigDecimal( EPrefs.VLRPERCDESC.toString() ) );
				prefs.put( EPrefs.CODPROT, rs.getInt( EPrefs.CODPROT.toString() ) );
				prefs.put( EPrefs.DIASPROT, rs.getInt( EPrefs.DIASPROT.toString() ) );
				prefs.put( EPrefs.CODBAIXADEV, rs.getInt( EPrefs.CODBAIXADEV.toString() ) );
				prefs.put( EPrefs.DIASBAIXADEV, rs.getInt( EPrefs.DIASBAIXADEV.toString() ) );
				prefs.put( EPrefs.MDECOB, rs.getString( EPrefs.MDECOB.toString() ) );
				prefs.put( EPrefs.CONVCOB, rs.getString( EPrefs.CONVCOB.toString() ) );
				prefs.put( EPrefs.ACEITE, rs.getString( EPrefs.ACEITE.toString() ) );
				
				retorno = true;
			}
			else {
				retorno = false;
				Funcoes.mensagemInforma( null, "Ajuste os par�metros antes de executar!" );
			}
			
			rs.close();
			ps.close();
			
			if ( ! con.getAutoCommit() ) {
				con.commit();
			}
		} catch ( Exception e ) {
			retorno = false;
			Funcoes.mensagemErro( this, "Carregando par�metros!\n" + e.getMessage() );
			e.printStackTrace();
			lbStatus.setText( "" );
		}
		
		return retorno;
	}

	protected ResultSet executeQuery() throws SQLException {

		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuilder sSQL = new StringBuilder();
		String sDtFiltro = "E".equals( rgData.getVlrString() ) ? "IR.DTITREC" : "IR.DTVENCITREC";

		if ( "00".equals( rgSitRemessa.getVlrString() ) ) {
			where = "AND ( FR.SITREMESSA IS NULL OR FR.SITREMESSA='00' ) AND ( FR.SITRETORNO IS NULL OR FR.SITRETORNO='00' ) ";
		}
		else if ( "01".equals( rgSitRemessa.getVlrString() ) ) {
			where = "AND ( FR.SITREMESSA IS NULL OR FR.SITREMESSA='01' ) ";
		}
		else if ( "02".equals( rgSitRemessa.getVlrString() ) ) {
			where = "AND ( FR.SITRETORNO IS NOT NULL AND FR.SITRETORNO<>'00' ) ";
		}

		sSQL.append( "SELECT IR.CODREC, IR.NPARCITREC, R.DOCREC, R.CODCLI, C.RAZCLI, IR.DTITREC, IR.DTVENCITREC," );
		sSQL.append( "IR.VLRAPAGITREC, FC.AGENCIACLI, FC.IDENTCLI, COALESCE(FR.SITREMESSA,'00') SITREMESSA, " );
		sSQL.append( "FR.SITRETORNO, COALESCE(COALESCE(FR.STIPOFEBRABAN,FC.STIPOFEBRABAN),'02') STIPOFEBRABAN, " );
		sSQL.append( "COALESCE(FC.TIPOREMCLI,'B') TIPOREMCLI, C.PESSOACLI, C.CPFCLI, C.CNPJCLI " );
		sSQL.append( "FROM VDCLIENTE C," );
		sSQL.append( "FNRECEBER R LEFT OUTER JOIN FNFBNCLI FC ON " );
		sSQL.append( "FC.CODEMP=R.CODEMPCL AND FC.CODFILIAL=R.CODFILIALCL AND FC.CODCLI=R.CODCLI ," );
		sSQL.append( "FNITRECEBER IR LEFT OUTER JOIN FNFBNREC FR ON " );
		sSQL.append( "FR.CODEMP=IR.CODEMP AND FR.CODFILIAL=IR.CODFILIAL AND " );
		sSQL.append( "FR.CODREC=IR.CODREC AND FR.NPARCITREC=IR.NPARCITREC AND " );
		sSQL.append( "FR.CODEMPBO=IR.CODEMPBO AND FR.CODFILIALBO=IR.CODFILIALBO AND FR.CODBANCO=IR.CODBANCO " );
		sSQL.append( "WHERE R.CODEMP=IR.CODEMP AND R.CODFILIAL=IR.CODFILIAL AND R.CODREC=IR.CODREC AND " );
		sSQL.append( "C.CODEMP=R.CODEMPCL AND C.CODFILIAL=R.CODFILIALCL AND C.CODCLI=R.CODCLI AND " );
		sSQL.append( sDtFiltro );
		sSQL.append( " BETWEEN ? AND ? AND IR.STATUSITREC IN ('R1','RL') AND " );
		sSQL.append( "IR.CODEMPBO=? AND IR.CODFILIALBO=? AND IR.CODBANCO=? " );
		sSQL.append( where );
		sSQL.append( "ORDER BY C.RAZCLI, R.CODREC, IR.NPARCITREC " );

		ps = con.prepareStatement( sSQL.toString() );
		ps.setDate( 1, Funcoes.dateToSQLDate( txtDtIni.getVlrDate() ) );
		ps.setDate( 2, Funcoes.dateToSQLDate( txtDtFim.getVlrDate() ) );
		ps.setInt( 3, Aplicativo.iCodEmp );
		ps.setInt( 4, ListaCampos.getMasterFilial( "FNITRECEBER" ) );
		ps.setString( 5, txtCodBanco.getVlrString() );
		rs = ps.executeQuery();
		return rs;
	}

	protected void carregaTab() {

		if ( txtCodBanco.getVlrString().trim().length() < 1 ) {
			Funcoes.mensagemErro( this, "O c�digo do banco � obrigatorio!" );
			return;
		}

		try {
			
			lbStatus.setText( "      carregando tabela ..." );

			tab.limpa();

			ResultSet rs = executeQuery();

			int i = 0;
			while ( rs.next() ) {

				tab.adicLinha();
				tab.setValor( new Boolean( true ), i, EColTab.COL_SEL.ordinal() );
				tab.setValor( rs.getString( "RAZCLI" ), i, EColTab.COL_RAZCLI.ordinal() );
				tab.setValor( new Integer( rs.getInt( "CODCLI" ) ), i, EColTab.COL_CODCLI.ordinal() );
				tab.setValor( new Integer( rs.getInt( "CODREC" ) ), i, EColTab.COL_CODREC.ordinal() );
				tab.setValor( rs.getString( "DOCREC" ), i, EColTab.COL_DOCREC.ordinal() );
				tab.setValor( new Integer( rs.getInt( "NPARCITREC" ) ), i, EColTab.COL_NRPARC.ordinal() );
				tab.setValor( Funcoes.bdToStr( rs.getBigDecimal( "VLRAPAGITREC" ) ), i, EColTab.COL_VLRAPAG.ordinal() );
				tab.setValor( rs.getDate( "DTITREC" ), i, EColTab.COL_DTREC.ordinal() );
				tab.setValor( rs.getDate( "DTVENCITREC" ), i, EColTab.COL_DTVENC.ordinal() );
				tab.setValor( rs.getString( "AGENCIACLI" ), i, EColTab.COL_AGENCIACLI.ordinal() );
				tab.setValor( rs.getString( "IDENTCLI" ), i, EColTab.COL_IDENTCLI.ordinal() );
				tab.setValor( rs.getString( "SITREMESSA" ), i, EColTab.COL_SITREM.ordinal() );
				tab.setValor( rs.getString( "SITRETORNO" ), i, EColTab.COL_SITRET.ordinal() );
				tab.setValor( rs.getString( "STIPOFEBRABAN" ), i, EColTab.COL_STIPOFEBRABAN.ordinal() );
				tab.setValor( rs.getString( "TIPOREMCLI" ), i, EColTab.COL_TIPOREMCLI.ordinal() );
				tab.setValor( rs.getString( "PESSOACLI" ), i, EColTab.COL_PESSOACLI.ordinal() );
				tab.setValor( rs.getString( "CPFCLI" ), i, EColTab.COL_CPFCLI.ordinal() );
				tab.setValor( rs.getString( "CNPJCLI" ), i, EColTab.COL_CNPJCLI.ordinal() );
				i++;
			}

 			rs.close();

			if ( !con.getAutoCommit() ) {
				con.commit();
			}

			if ( i > 0 ) {
				lbStatus.setText( "     tabela carregada com " + i + " itens..." );
			}
			else {
				lbStatus.setText( "" );
			}
			
			if ( "2".equals( rgTipoRemessa.getVlrString() ) ) {
				selecionaNada();
			}

		} catch ( Exception e ) {
			Funcoes.mensagemErro( this, "Erro ao busca dados!\n" + e.getMessage() );
			e.printStackTrace();
			lbStatus.setText( "" );
		} finally {
			System.gc();
		}

	}
	
	private void selecionaTudo() {
	
		for ( int i = 0; i < tab.getNumLinhas(); i++ ) {
			tab.setValor( new Boolean( true ), i, 0 );
		}
	}
	

	private void selecionaNada() {
	
		for ( int i = 0; i < tab.getNumLinhas(); i++ ) {
			tab.setValor( new Boolean( false ), i, 0 );
		}
	}

	protected boolean consisteExporta( HashSet<FbnUtil.StuffCli> hsCli, HashSet<FbnUtil.StuffRec> hsRec, boolean completartabela ) {

		boolean retorno = true;
		Vector<?> vLinha = null;

		for ( int i = 0; i < tab.getNumLinhas(); i++ ) {

			vLinha = tab.getLinha( i );

			if ( (Boolean) vLinha.elementAt( EColTab.COL_SEL.ordinal() ) ) {
				if ( completartabela ) {
					if ( "".equals( (String) vLinha.elementAt( EColTab.COL_AGENCIACLI.ordinal() ) ) 
							|| "".equals( (String) vLinha.elementAt( EColTab.COL_IDENTCLI.ordinal() ) ) ) {
						if ( ! completaTabela( i, 
								(Integer) vLinha.elementAt( EColTab.COL_CODCLI.ordinal() ), 
								(String) vLinha.elementAt( EColTab.COL_RAZCLI.ordinal() ), 
								(String) vLinha.elementAt( EColTab.COL_AGENCIACLI.ordinal() ), 
								(String) vLinha.elementAt( EColTab.COL_IDENTCLI.ordinal() ),
								(String) vLinha.elementAt( EColTab.COL_STIPOFEBRABAN.ordinal() ) ) ) {
							retorno = false;
							break;
						}
					}
				}
				hsCli.add( new FbnUtil().new StuffCli( 
						(Integer) vLinha.elementAt( EColTab.COL_CODCLI.ordinal() ), 
						new String[] { txtCodBanco.getVlrString(), TIPO_FEBRABAN, 
						(String) vLinha.elementAt( EColTab.COL_STIPOFEBRABAN.ordinal() ),
						(String) vLinha.elementAt( EColTab.COL_AGENCIACLI.ordinal() ), 
						(String) vLinha.elementAt( EColTab.COL_IDENTCLI.ordinal() ),
						(String) vLinha.elementAt( EColTab.COL_TIPOREMCLI.ordinal() ) } ) );
				hsRec.add( new FbnUtil().new StuffRec( 
						(Integer) vLinha.elementAt( EColTab.COL_CODREC.ordinal() ), 
						(Integer) vLinha.elementAt( EColTab.COL_NRPARC.ordinal() ),
						new String[] { txtCodBanco.getVlrString(), TIPO_FEBRABAN, 
						(String) vLinha.elementAt( EColTab.COL_STIPOFEBRABAN.ordinal() ), 
						(String) vLinha.elementAt( EColTab.COL_SITREM.ordinal() ), 
						String.valueOf( (Integer) vLinha.elementAt( EColTab.COL_CODCLI.ordinal() ) ), 
						(String) vLinha.elementAt( EColTab.COL_AGENCIACLI.ordinal() ),
						(String) vLinha.elementAt( EColTab.COL_IDENTCLI.ordinal() ), 
						Funcoes.dataAAAAMMDD( (Date) vLinha.elementAt( EColTab.COL_DTVENC.ordinal() ) ), 
						Funcoes.strToBd( vLinha.elementAt( EColTab.COL_VLRAPAG.ordinal() )).toString(),
						(String) vLinha.elementAt( EColTab.COL_PESSOACLI.ordinal() ),
						(String) vLinha.elementAt( EColTab.COL_CPFCLI.ordinal() ),
						(String) vLinha.elementAt( EColTab.COL_CNPJCLI.ordinal() ),
						rgTipoRemessa.getVlrString(),
						String.valueOf( (Integer) vLinha.elementAt( EColTab.COL_CODREC.ordinal() ) ),
						Funcoes.dataAAAAMMDD( (Date) vLinha.elementAt( EColTab.COL_DTREC.ordinal() ) ),
						String.valueOf( vLinha.elementAt( EColTab.COL_NRPARC.ordinal() ) ) } 
					) );
		   }
		}
		if ( retorno ) {
			retorno = persisteDados( hsCli, hsRec );
		}

		return retorno;
	}
	
	protected boolean completaTabela( final int linha, final Integer codCli, final String razCli, 
			final String agenciaCli, final String identCli, final String subTipo ) {

		boolean retorno = true;

		Object[] valores = DLIdentCli.execIdentCli( this, codCli, razCli, agenciaCli, identCli, subTipo );
		retorno = ( (Boolean) valores[ 0 ] ).booleanValue();

		if ( retorno ) {
			ajustaClientes( codCli, (String) valores[ 1 ], (String) valores[ 2 ], (String) valores[ 3 ] );
		}
		else {
			tab.setValor( false, linha, EColTab.COL_SEL.ordinal() );
		}

		return retorno;
	}

	protected void ajustaClientes( final Integer codCli, final String agenciaCli, final String identCli, final String subTipo ) {

		for ( int i = 0; i < tab.getNumLinhas(); i++ ) {
			if ( ( (Boolean) tab.getValor( i, EColTab.COL_SEL.ordinal() ) ).booleanValue() 
					&& codCli.equals( (Integer) tab.getValor( i, EColTab.COL_CODCLI.ordinal() ) ) ) {
				tab.setValor( agenciaCli, i, EColTab.COL_AGENCIACLI.ordinal() );
				tab.setValor( identCli, i, EColTab.COL_IDENTCLI.ordinal() );
				tab.setValor( subTipo, i, EColTab.COL_STIPOFEBRABAN.ordinal() );
			}
		}
	}

	protected boolean persisteDados( final HashSet<FbnUtil.StuffCli> hsCli, 
			final HashSet<FbnUtil.StuffRec> hsRec ) {

		boolean retorno = true;
		for ( FbnUtil.StuffCli stfCli : hsCli ) {
			retorno = updateCliente( stfCli.getCodigo(), 
					stfCli.getArgs()[ FbnUtil.EColcli.CODBANCO.ordinal() ], 
					stfCli.getArgs()[ FbnUtil.EColcli.TIPOFEBRABAN.ordinal() ], 
					stfCli.getArgs()[ FbnUtil.EColcli.STIPOFEBRABAN.ordinal() ], 
					stfCli.getArgs()[ FbnUtil.EColcli.AGENCIACLI.ordinal() ], 
					stfCli.getArgs()[ FbnUtil.EColcli.IDENTCLI.ordinal() ], 
					stfCli.getArgs()[ FbnUtil.EColcli.TIPOREMCLI.ordinal() ] );
			if ( ! retorno ) {
				retorno = false;
				break;
			}
		}
		if ( retorno ) {
			for ( FbnUtil.StuffRec stfRec : hsRec ) {
				retorno = updateReceber( stfRec.getCodrec(), stfRec.getNParcitrec(), 
						stfRec.getArgs()[ FbnUtil.EColrec.CODBANCO.ordinal() ], 
						stfRec.getArgs()[ FbnUtil.EColrec.TIPOFEBRABAN.ordinal() ], 
						stfRec.getArgs()[ FbnUtil.EColrec.STIPOFEBRABAN.ordinal() ], 
						stfRec.getArgs()[ FbnUtil.EColrec.SITREMESSA.ordinal() ] );
				if ( !retorno ) {
					retorno = false;
					break;
				}
			}
		}
		return retorno;
	}	
	
	protected boolean updateCliente( int codCli, String codBanco, String tipoFebraban, 
			String stipoFebraban, String agenciaCli, String identCli, String tipoRemCli ) {

		boolean retorno = false;

		try {
			
			StringBuilder sql = new StringBuilder();
			sql.append( "SELECT AGENCIACLI, IDENTCLI, STIPOFEBRABAN, TIPOREMCLI FROM FNFBNCLI " );
			sql.append( "WHERE CODEMP=? AND CODFILIAL=? AND CODCLI=? " );
			sql.append( "AND CODEMPPF=? AND CODFILIALPF=? AND CODEMPBO=? AND CODFILIALBO=? AND CODBANCO=? AND TIPOFEBRABAN=?" );
						
			PreparedStatement ps = con.prepareStatement( sql.toString() );
			ps.setInt( 1, Aplicativo.iCodEmp );
			ps.setInt( 2, ListaCampos.getMasterFilial( "VDCLIENTE" ) );
			ps.setInt( 3, codCli );
			ps.setInt( 4, Aplicativo.iCodEmp );
			ps.setInt( 5, ListaCampos.getMasterFilial( "SGITPREFERE6" ) );
			ps.setInt( 6, Aplicativo.iCodEmp );
			ps.setInt( 7, ListaCampos.getMasterFilial( "FNBANCO" ) );
			ps.setString( 8, codBanco );
			ps.setString( 9, tipoFebraban );
			
			ResultSet rs = ps.executeQuery();
			if ( rs.next() ) {
				
				if ( ( ! agenciaCli.equals( rs.getString( "AGENCIACLI" ) ) ) 
						|| ( !identCli.equals( rs.getString( "IDENTCLI" ) ) ) 
							|| ( !stipoFebraban.equals( rs.getString( "STIPOFEBRABAN" ) ) ) 
								|| ( !tipoRemCli.equals( rs.getString( "TIPOREMCLI" ) ) ) ) {
					
					StringBuilder sqlup = new StringBuilder();
					sqlup.append( "UPDATE FNFBNCLI SET AGENCIACLI=?, IDENTCLI=?, STIPOFEBRABAN=?, TIPOREMCLI=? " );
					sqlup.append( "WHERE CODEMP=? AND CODFILIAL=? AND CODCLI=? " );
					sqlup.append( "AND CODEMPPF=? AND CODFILIALPF=? AND CODEMPBO=? AND CODFILIALBO=? AND CODBANCO=? AND TIPOFEBRABAN=?" ); 
					
					ps = con.prepareStatement( sqlup.toString() );
					ps.setString( 1, agenciaCli );
					ps.setString( 2, identCli );
					ps.setString( 3, stipoFebraban );
					ps.setString( 4, tipoRemCli );
					ps.setInt( 5, Aplicativo.iCodEmp );
					ps.setInt( 6, ListaCampos.getMasterFilial( "VDCLIENTE" ) );
					ps.setInt( 7, codCli );
					ps.setInt( 8, Aplicativo.iCodEmp );
					ps.setInt( 9, ListaCampos.getMasterFilial( "SGITPREFERE6" ) );
					ps.setInt( 10, Aplicativo.iCodEmp );
					ps.setInt( 11, ListaCampos.getMasterFilial( "FNBANCO" ) );
					ps.setString( 12, codBanco );
					ps.setString( 13, tipoFebraban );
					ps.executeUpdate();
				}
			}
			else {
				
				StringBuilder sqlin = new StringBuilder();
				sqlin.append( "INSERT INTO FNFBNCLI (AGENCIACLI, IDENTCLI, CODEMP, CODFILIAL, " ); 
				sqlin.append( "CODCLI, CODEMPPF, CODFILIALPF, CODEMPBO, CODFILIALBO, CODBANCO, " );
				sqlin.append( "TIPOFEBRABAN, STIPOFEBRABAN, TIPOREMCLI) " );
				sqlin.append( "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)" );
				
				ps = con.prepareStatement( sqlin.toString() );
				ps.setString( 1, agenciaCli );
				ps.setString( 2, identCli );
				ps.setInt( 3, Aplicativo.iCodEmp );
				ps.setInt( 4, ListaCampos.getMasterFilial( "VDCLIENTE" ) );
				ps.setInt( 5, codCli );
				ps.setInt( 6, Aplicativo.iCodEmp );
				ps.setInt( 7, ListaCampos.getMasterFilial( "SGITPREFERE6" ) );
				ps.setInt( 8, Aplicativo.iCodEmp );
				ps.setInt( 9, ListaCampos.getMasterFilial( "FNBANCO" ) );
				ps.setString( 10, codBanco );
				ps.setString( 11, tipoFebraban );
				ps.setString( 12, stipoFebraban );
				ps.setString( 13, tipoRemCli );

				ps.executeUpdate();
			}
			if ( ! con.getAutoCommit() ) {
				con.commit();				
			}
			
			retorno = true;
			
		} catch ( SQLException e ) {
			Funcoes.mensagemErro( this, "Erro atualizando cliente!\n" + e.getMessage() );
			e.printStackTrace();
		}

		return retorno;
	}

	protected boolean updateReceber( int codRec, int nParcitrec, String codBanco, 
			String tipoFebraban, String stipoFebraban, String sitRemessa ) {

		boolean retorno = false;
		
		try {
			
			StringBuilder sql = new StringBuilder();
			sql.append( "SELECT CODBANCO, TIPOFEBRABAN, STIPOFEBRABAN, SITREMESSA " );
			sql.append( "FROM FNFBNREC WHERE CODEMP=? AND CODFILIAL=? AND CODREC=? AND NPARCITREC=?" );
			
			PreparedStatement ps = con.prepareStatement( sql.toString() );
			ps.setInt( 1, Aplicativo.iCodEmp );
			ps.setInt( 2, ListaCampos.getMasterFilial( "FNITRECEBER" ) );
			ps.setInt( 3, codRec );
			ps.setInt( 4, nParcitrec );
			
			ResultSet rs = ps.executeQuery();
			if ( rs.next() ) {
				if ( ( !codBanco.equals( rs.getString( "CODBANCO" ) ) ) || 
						( !tipoFebraban.equals( rs.getString( "TIPOFEBRABAN" ) ) ) || 
						( !stipoFebraban.equals( rs.getString( "STIPOFEBRABAN" ) ) ) || 
						( !sitRemessa.equals( rs.getString( "SITREMESSA" ) ) ) ) {
					
					StringBuilder sqlup = new StringBuilder();
					sqlup.append( "UPDATE FNFBNREC SET CODBANCO=?, TIPOFEBRABAN=?, STIPOFEBRABAN=?, SITREMESSA=? " );
					sqlup.append( "WHERE CODEMP=? AND CODFILIAL=? AND CODREC=? AND NPARCITREC=?" );
					
					ps = con.prepareStatement( sqlup.toString() );
					ps.setString( 1, codBanco );
					ps.setString( 2, tipoFebraban );
					ps.setString( 3, stipoFebraban );
					ps.setString( 4, sitRemessa );
					ps.setInt( 5, Aplicativo.iCodEmp );
					ps.setInt( 6, ListaCampos.getMasterFilial( "FNITRECEBER" ) );
					ps.setInt( 7, codRec );
					ps.setInt( 8, nParcitrec );
					ps.executeUpdate();
				}
			}
			else {
				
				StringBuilder sqlin = new StringBuilder();
				sqlin.append( "INSERT INTO FNFBNREC (CODEMP, CODFILIAL, CODREC, NPARCITREC, " ); 
				sqlin.append( "CODEMPPF, CODFILIALPF, CODEMPBO, CODFILIALBO, CODBANCO, " );
				sqlin.append( "TIPOFEBRABAN, STIPOFEBRABAN, SITREMESSA) " );
				sqlin.append( "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)" );
				
				ps = con.prepareStatement( sqlin.toString() );
				ps.setInt( 1, Aplicativo.iCodEmp );
				ps.setInt( 2, ListaCampos.getMasterFilial( "FNFBNREC" ) );
				ps.setInt( 3, codRec );
				ps.setInt( 4, nParcitrec );
				ps.setInt( 5, Aplicativo.iCodEmp );
				ps.setInt( 6, ListaCampos.getMasterFilial( "SGITPREFERE6" ) );
				ps.setInt( 7, Aplicativo.iCodEmp );
				ps.setInt( 8, ListaCampos.getMasterFilial( "FNBANCO" ) );
				ps.setString( 9, codBanco );
				ps.setString( 10, tipoFebraban );
				ps.setString( 11, stipoFebraban );
				ps.setString( 12, sitRemessa );
				ps.executeUpdate();
			}
			
			if ( ! con.getAutoCommit() ) {
				con.commit();
			}
			
			retorno = true;

		} catch ( SQLException e ) {
			Funcoes.mensagemErro( this, "Erro atualizando situa��o do contas a receber!\n" + e.getMessage() );
		}

		return retorno;
	}
	
	protected boolean updatePrefere() {
		
		boolean retorno = true;
		
		try {
			StringBuilder sql = new StringBuilder();
			sql.append( "UPDATE SGITPREFERE6 I SET NROSEQ=? " );
			sql.append( "WHERE I.CODEMP=? AND I.CODFILIAL=? " );
			sql.append( "AND I.CODEMPBO=? AND I.CODFILIALBO=? AND I.CODBANCO=? AND I.TIPOFEBRABAN=?" );
			
			PreparedStatement ps = con.prepareStatement( sql.toString() );
			ps.setInt( 1, (Integer) prefs.get(FbnUtil.EPrefs.NROSEQ) );
			ps.setInt( 2, Aplicativo.iCodEmp);
			ps.setInt( 3, ListaCampos.getMasterFilial( "SGITPREFERE6" ) );
			ps.setInt( 4, Aplicativo.iCodEmp );
			ps.setInt( 5, ListaCampos.getMasterFilial( "FNBANCO" ) );
			ps.setString( 6, (String) prefs.get( FbnUtil.EPrefs.CODBANCO ) );
			ps.setString( 7, TIPO_FEBRABAN );
			ps.executeUpdate();
			ps.close();
			if ( ! con.getAutoCommit() ) {
				con.commit();
			}
		}
		catch ( SQLException e ) {
			retorno = false;
			Funcoes.mensagemErro( this, "Erro atualizando par�metros!\n" + e.getMessage() );
		}

		return retorno;
	}
	
	abstract protected boolean execExporta();
	
	abstract public void imprimir( boolean bVisualizar ); 
	
	public void valorAlterado( RadioGroupEvent e ) {

		if ( "1".equals( rgTipoRemessa.getVlrString() ) ) {
			selecionaTudo();
		}
		else if ( "2".equals( rgTipoRemessa.getVlrString() ) ) {
			selecionaNada();
		}
	}

	public void actionPerformed( ActionEvent evt ) {
	
		if ( evt.getSource() == btCarrega ) {
			carregaTab();
		}
		else if ( evt.getSource() == btSelTudo ) {
			selecionaTudo();
		}
		else if ( evt.getSource() == btSelNada ) {
			selecionaNada();
		}
		else if ( evt.getSource() == btExporta ) {
			execExporta();
		}
		else if( evt.getSource() == btVisImp ){
			imprimir( true );
		}
	}

	public void mouseClicked( MouseEvent e ) { }

	public void mouseEntered( MouseEvent e ) { }

	public void mouseExited( MouseEvent e ) { }

	public void mousePressed( MouseEvent e ) { }

	public void mouseReleased( MouseEvent e ) { }

	public void setConexao( Connection cn ) {

		super.setConexao( cn );
		lcBanco.setConexao( cn );
	}
	
	enum EColTab {
		
		COL_SEL, COL_RAZCLI, COL_CODCLI, COL_CODREC, COL_DOCREC, COL_NRPARC, COL_VLRAPAG,
		COL_DTREC, COL_DTVENC, COL_AGENCIACLI, COL_IDENTCLI, COL_SITREM, COL_SITRET,
		COL_STIPOFEBRABAN, COL_TIPOREMCLI, COL_PESSOACLI, COL_CPFCLI, COL_CNPJCLI, COL_CARTEIRA;
	}

}
