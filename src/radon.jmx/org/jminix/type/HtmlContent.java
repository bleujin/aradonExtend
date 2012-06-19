/* 
 * ------------------------------------------------------------------------------------------------
 * Copyright 2011 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id: HtmlContent.java,v 1.1 2012/02/17 11:58:59 bleujin Exp $
 * ------------------------------------------------------------------------------------------------
 *
 */
package org.jminix.type;

import java.io.Serializable;

/**
 * Marker interface for text that must be rendered as an HTML content by the console. The <code>toString()</code> method is called
 * to retrieve the HTML text.
 * 
 * <p>
 * Security Note: Beware of cross-site scripting. Take care to use this type only for content that you master because
 * the content and scripts are rendered as-is without escaping.
 * 
 * @author bovetl
 * @version $Revision: 1.1 $
 * @see <script>links('$HeadURL: https://jminix.googlecode.com/svn/tags/jminix-1.0.0/src/main/java/org/jminix/type/HtmlContent.java $');</script>
 */
public interface HtmlContent extends Serializable {

}
