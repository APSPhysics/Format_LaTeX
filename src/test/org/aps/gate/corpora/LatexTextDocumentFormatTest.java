package org.aps.gate.corpora;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.GateConstants;
import gate.corpora.DocumentContentImpl;
import gate.corpora.DocumentImpl;
import junit.framework.TestCase;

public class LatexTextDocumentFormatTest  extends TestCase {
    public void testRemoveIgnorableContent() throws Exception {
    	String testString = "%ignore this\nnot this\n%ignore more\n";
    	assertEquals("not this\n", LatexTextDocumentFormat.removeIgnorableContent(testString));

    	String testString2 = "But leave inline\ncomments alone%Alone\n";
    	assertEquals(testString2, LatexTextDocumentFormat.removeIgnorableContent(testString2));
    }
    
    public void testAddTitleAnnotation() throws Exception {
    	Document doc = new DocumentImpl();
    	doc.setContent(new DocumentContentImpl("\\begin{document}\n\\title{This is a title}\n\\end{document}\n"));
    	LatexTextDocumentFormat.addTexAnnotations(doc);
    	AnnotationSet annots = doc.getAnnotations(GateConstants.ORIGINAL_MARKUPS_ANNOT_SET_NAME);
    	AnnotationSet titleAnnots = annots.get("title");
    	assertEquals(1, titleAnnots.size());
    	Annotation annot = titleAnnots.iterator().next();
    	assertEquals(24, annot.getStartNode().getOffset().intValue());
    	assertEquals(39, annot.getEndNode().getOffset().intValue());
    }
    
    public void testAddAuthorAnnotation() throws Exception {
    	Document doc = new DocumentImpl();
    	doc.setContent(new DocumentContentImpl("\\begin{document}\n\\author{A.Author}\n\\author{B.Author}\\end{document}\n"));
    	LatexTextDocumentFormat.addTexAnnotations(doc);
    	AnnotationSet annots = doc.getAnnotations(GateConstants.ORIGINAL_MARKUPS_ANNOT_SET_NAME);
    	AnnotationSet authAnnots = annots.get("author");
    	assertEquals(2, authAnnots.size());
    	Annotation annot = authAnnots.iterator().next();
    	assertEquals(25, annot.getStartNode().getOffset().intValue());
    	assertEquals(33, annot.getEndNode().getOffset().intValue());
    }
    
    public void testAddDocumentAnnotation() throws Exception {
    	Document doc = new DocumentImpl();
    	doc.setContent(new DocumentContentImpl("\\begin{document}\nDocument Content\n\\end{document}\n"));
    	LatexTextDocumentFormat.addTexAnnotations(doc);
    	AnnotationSet annots = doc.getAnnotations(GateConstants.ORIGINAL_MARKUPS_ANNOT_SET_NAME);
    	AnnotationSet docAnnots = annots.get("document");
    	assertEquals(1, docAnnots.size());
    	Annotation annot = docAnnots.iterator().next();
    	assertEquals(17, annot.getStartNode().getOffset().intValue());
    	assertEquals(33, annot.getEndNode().getOffset().intValue());
    }
    
    public void testAddAbstractAnnotation() throws Exception {
    	Document doc = new DocumentImpl();
    	doc.setContent(new DocumentContentImpl("\\begin{document}\nDocument Content\n\\begin{abstract}\nTest Abstract\n\\end{abstract}\n\\end{document}\n"));
    	LatexTextDocumentFormat.addTexAnnotations(doc);
    	AnnotationSet annots = doc.getAnnotations(GateConstants.ORIGINAL_MARKUPS_ANNOT_SET_NAME);
    	AnnotationSet docAnnots = annots.get("abstract");
    	assertEquals(1, docAnnots.size());
    	Annotation annot = docAnnots.iterator().next();
    	assertEquals(51, annot.getStartNode().getOffset().intValue());
    	assertEquals(64, annot.getEndNode().getOffset().intValue());
    }
    
    public void testAddTableAnnotation() throws Exception {
    	Document doc = new DocumentImpl();
    	doc.setContent(new DocumentContentImpl("\\begin{document}\n\\begin{table}\ntable 1\n\\end{table}\n\\begin{table}\ntable 2\n\\end{table}\n\\end{document}\n"));
    	LatexTextDocumentFormat.addTexAnnotations(doc);
    	AnnotationSet annots = doc.getAnnotations(GateConstants.ORIGINAL_MARKUPS_ANNOT_SET_NAME);
    	AnnotationSet docAnnots = annots.get("table");
    	assertEquals(2, docAnnots.size());
    }

    public void testAddFigureStarAnnotation() throws Exception {
    	Document doc = new DocumentImpl();
    	doc.setContent(new DocumentContentImpl("\\begin{document}\n\\begin{figure*}\nfigure stuff\n\\end{figure*}\\end{document}\n"));
    	LatexTextDocumentFormat.addTexAnnotations(doc);
    	AnnotationSet annots = doc.getAnnotations(GateConstants.ORIGINAL_MARKUPS_ANNOT_SET_NAME);
    	AnnotationSet authAnnots = annots.get("figure*");
    	assertEquals(1, authAnnots.size());
    }
    
    public void testAddMathAnnotations() throws Exception {
    	Document doc = new DocumentImpl();
    	doc.setContent(new DocumentContentImpl("\\begin{document}\n$x + y$\n$\\frac{1/\nx}$\nFor a \\$300 fee\n\\end{document}\n"));
    	LatexTextDocumentFormat.addTexAnnotations(doc);
    	AnnotationSet annots = doc.getAnnotations(GateConstants.ORIGINAL_MARKUPS_ANNOT_SET_NAME);
    	AnnotationSet docAnnots = annots.get("math");
    	assertEquals(2, docAnnots.size());
    	Annotation annot = docAnnots.iterator().next();
    	assertEquals(18, annot.getStartNode().getOffset().intValue());
    	assertEquals(23, annot.getEndNode().getOffset().intValue());
    }
}
