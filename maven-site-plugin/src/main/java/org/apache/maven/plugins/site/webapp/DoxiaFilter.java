package org.apache.maven.plugins.site.webapp;

/*
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.doxia.module.xhtml.decoration.render.RenderingContext;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.doxia.siterenderer.RendererException;
import org.apache.maven.doxia.siterenderer.SiteRenderingContext;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Render a page as requested.
 *
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 */
public class DoxiaFilter
    implements Filter
{
    private File siteDirectory;

    private Renderer siteRenderer;

    private SiteRenderingContext context;

    private Map documents;

    public void init( FilterConfig filterConfig )
        throws ServletException
    {
        ServletContext servletContext = filterConfig.getServletContext();
        siteDirectory = (File) servletContext.getAttribute( "siteDirectory" );
        siteRenderer = (Renderer) servletContext.getAttribute( "siteRenderer" );
        context = (SiteRenderingContext) servletContext.getAttribute( "context" );
        documents = (Map) servletContext.getAttribute( "documents" );
    }

    public void doFilter( ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain )
        throws IOException, ServletException
    {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        String path = req.getServletPath().substring( 1 );

        if ( documents.containsKey( path ) )
        {
            // TODO: documents are not right for the locale
            context.setLocale( req.getLocale() );

            try
            {
                siteRenderer.renderDocument( servletResponse.getWriter(), (RenderingContext) documents.get( path ),
                                             context );
            }
            catch ( RendererException e )
            {
                throw new ServletException( e );
            }
        }
        else
        {
            filterChain.doFilter( servletRequest, servletResponse );
        }
    }

    public void destroy()
    {
    }
}
