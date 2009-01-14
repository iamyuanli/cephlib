package fr.cephb.lindenb.dochandler.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="cell")
@SuppressWarnings("unused")
public class Cell
	{
	private long id;
	private int row;
	private int col;
	private int documentId;
	private String content;
	
	
	public Cell()
		{
		
		}
	
	
	
	public Cell( int documentId,int col, int row, String content)
		{
		this.col = col;
		this.content = content;
		this.documentId = documentId;
		this.row = row;
		}



	@Id
	@Column(name="id")
	@GeneratedValue(strategy=GenerationType.AUTO)
	public long getId() {
		return id;
	}
	
	private void setId(long id) {
		this.id = id;
	}
	
	@Column(name="rowIndex")
	public int getRow() {
		return row;
	}
	private void setRow(int row) {
		this.row = row;
	}
	
	@Column(name="colIndex")
	public int getColumn() {
		return col;
	}
	private void setColumn(int col) {
		this.col = col;
	}
	
	@Column(name="document_id")
	public int getDocumentId() {
		return documentId;
	}
	private void setDocumentId(int documentId) {
		this.documentId = documentId;
	}
	
	@Column(name="content",length=50,nullable=false)
	public String getContent() {
		return content;
	}
	private void setContent(String content) {
		this.content = content;
	}
	
	@Override
	public String toString() {
		return getContent();
		}
	}
