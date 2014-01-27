/*
 *  LatexTextDocumentFormat.java
 *
 *  Copyright (c) 2014, The American Physical Society.
 *
 *  Arthur Smith, January 23, 2014
 *
 */
package org.aps.gate.corpora;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.GateConstants;
import gate.Resource;
import gate.corpora.DocumentContentImpl;
import gate.corpora.MimeType;
import gate.corpora.TextualDocumentFormat;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.AutoInstance;
import gate.creole.metadata.CreoleResource;
import gate.util.DocumentFormatException;
import gate.util.InvalidOffsetException;

/**
 * A document format analyser for LaTeX documents. Use mime type value 
 * "application/x-tex", or file extension ".tex" to access this document
 * format. 
 */
@CreoleResource(name="GATE .tex document format",
  comment="<html>Load this to allow the opening of TeX/LaTeX documents, " +
      "and choose the mime type <strong>\"application/x-tex\"</strong>, or use " +
      "the correct file extension.", 
  autoinstances = {@AutoInstance(hidden=true)},
  isPrivate = true)
public class LatexTextDocumentFormat extends TextualDocumentFormat {
  private static final long serialVersionUID = 1L;
  
  protected static final Logger logger = Logger.getLogger(
      LatexTextDocumentFormat.class);
  
  /* (non-Javadoc)
   * @see gate.DocumentFormat#supportsRepositioning()
   */
  @Override
  public Boolean supportsRepositioning() {
    return false;
  }

  
  /* (non-Javadoc)
   * @see gate.corpora.TextualDocumentFormat#init()
   */
  @Override
  public Resource init() throws ResourceInstantiationException {
    MimeType mime = new MimeType("application","x-tex");
    // Register the class handler for this mime type
    mimeString2ClassHandlerMap.put(mime.getType()+ "/" + mime.getSubtype(),
                                                                          this);
    // Register the mime type with mine string
    mimeString2mimeTypeMap.put(mime.getType() + "/" + mime.getSubtype(), mime);
    // Register file sufixes for this mime type
    suffixes2mimeTypeMap.put("tex", mime);
    // Set the mimeType for this language resource
    setMimeType(mime);
    return this;
  }  
  
  /* (non-Javadoc)
   * @see gate.corpora.TextualDocumentFormat#unpackMarkup(gate.Document)
   */
  @Override
  public void unpackMarkup(Document doc) throws DocumentFormatException {
    try {
      String documentContent = removeIgnorableContent(doc.getContent().toString());
      doc.setContent(new DocumentContentImpl(documentContent));
      addTexAnnotations(doc);
    } catch(IOException e) {
      throw new DocumentFormatException("Error while unpacking markup",e); 
    }
    
    // now let the text unpacker also do its job
    super.unpackMarkup(doc);
	}

	public static String removeIgnorableContent(String documentContent)
			throws IOException {
		BufferedReader content = new BufferedReader(new StringReader(
				documentContent));
		// Remove lines with leading % character (comment lines)
		String line = content.readLine();
		StringBuilder docText = new StringBuilder();
		Pattern linePatt = Pattern.compile("%");
		while (line != null) {
			Matcher matcher = linePatt.matcher(line);
			if (!matcher.lookingAt()) {
				docText.append(line + "\n");
			}
			line = content.readLine();
		}
		return docText.toString();
	}
	
	public static void addTexAnnotations(Document doc) throws DocumentFormatException {
		String documentContent = doc.getContent().toString();
		AnnotationSet origMkups = doc.getAnnotations(GateConstants.ORIGINAL_MARKUPS_ANNOT_SET_NAME);
		addInlineCommandMarkup(documentContent, origMkups);
		addBeginEndCommandMarkup(documentContent, origMkups);
		addMathAnnotations(documentContent, origMkups);
	}

	private static void addInlineCommandMarkup(String content, AnnotationSet origMkups)
			throws DocumentFormatException {
		Pattern commandPatt = Pattern.compile("\\\\([a-z]+)\\s*\\{");
		Matcher matcher = commandPatt.matcher(content);
		while (matcher.find()) {
			String texCommand = matcher.group(1);
			if ("begin".equals(texCommand) || "end".equals(texCommand)) continue; // handle begin/end separately
			int first = matcher.end();
			int last = first + findMatchingCloseBracket(content.substring(first));
			addAnnotationWithNoFeatures(origMkups, texCommand, first, last);
		}
	}

	private static int findMatchingCloseBracket(String str) {
		char[] chars = str.toCharArray();
		int matchingClose = 0;
		int level = 1;
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == '\\') {
				i++; // Skip next char too
				continue;
			} else if (chars[i] == '{') {
				level++;
				continue;
			} else if (chars[i] == '}') {
				level--;
				if (level == 0) {
					matchingClose = i;
					break;
				}
			}
		}
		return matchingClose;
	}

	private static void addBeginEndCommandMarkup(String content, AnnotationSet origMkups)
			throws DocumentFormatException {
		Pattern commandPatt = Pattern.compile("(?s)\\\\begin\\{([a-z]+\\*?)\\}\\s*(.*?)\\s*\\\\end\\{\\1\\}");
		Matcher matcher = commandPatt.matcher(content);
		int offset = 0;
		while (matcher.find(offset)) {
			String texBeginMarkup = matcher.group(1);
			int first = matcher.start(2);
			int last = matcher.end(2);
			offset = matcher.start() + 1; // Match within last match if possible.
			addAnnotationWithNoFeatures(origMkups, texBeginMarkup, first, last);
		}
	}

	private static void addMathAnnotations(String content, AnnotationSet origMkups)
			throws DocumentFormatException {
		Pattern mathPatt = Pattern.compile("(?s)[^\\\\\\$]\\$([^\\$]*)\\$");
		Matcher matcher = mathPatt.matcher(content);
		while (matcher.find()) {
			int first = matcher.start(1);
			int last = matcher.end(1);
			addAnnotationWithNoFeatures(origMkups, "math", first, last);
		}	
	}

	private static void addAnnotationWithNoFeatures(AnnotationSet origMkups,
			String texCommand, int first, int last)
			throws DocumentFormatException {
		try {
			origMkups.add((long) first, (long) last, texCommand, Factory.newFeatureMap());
		} catch (InvalidOffsetException e) {
			logger.error("bad ranges in text " + first + ":" + last);
			throw new DocumentFormatException("Error while unpacking markup",e); 
		}
	}
}
