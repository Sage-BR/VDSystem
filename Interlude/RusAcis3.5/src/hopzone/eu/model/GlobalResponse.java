/*
 * Copyright (c) 2024 DenArt Designs
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package hopzone.eu.model;

import hopzone.eu.util.Json;
import hopzone.eu.vote.VDSystem;
import hopzone.eu.Configurations;
import hopzone.eu.model.base.IResponse;

/**
 * @Author Nightwolf
 * iToPz Discord: https://discord.gg/KkPms6B5aE
 * @Author Rationale
 * Base structure credits goes on Rationale Discord: Rationale#7773
 * <p>
 * VDS Stands for: Vote Donation System
 * Script website: https://itopz.com/
 * Partner website: https://hopzone.eu/
 * Script version: 1.8
 * Pack Support: aCis 401
 * <p>
 * Freemium Donate Panel V4: https://www.denart-designs.com/
 * Download: https://mega.nz/folder/6oxUyaIJ#qQDUXeoXlPvBjbPMDYzu-g
 * Buy: https://shop.denart-designs.com/product/auto-donate-panel-v4/
 *
 * Quick Guide: https://github.com/nightw0lv/VDSystem/tree/master/Guide
 */
public class GlobalResponse extends IResponse
{
	// private variables
	private static int _responseCode, _serverVotes, _serverRank, _serverNeededVotes, _serverNextRank;

	/**
	 * constructor
	 *
	 * @param url string
	 */
	public GlobalResponse(final String url)
	{
		super(url);
	}

	/**
	 * override onFetch
	 *
	 * @param responseCode int
	 * @param response     Json object
	 */
	@Override
	public void onFetch(final String TOPSITE, final int responseCode, final Json response)
	{
		_responseCode = responseCode;
		_serverVotes = response.getInteger(TOPSITE.toLowerCase() + "_votes");
		_serverRank = response.getInteger(TOPSITE.toLowerCase() + "_rank");
		_serverNeededVotes = response.getInteger(TOPSITE.toLowerCase() + "_rank_votes");
		_serverNextRank = response.getInteger(TOPSITE.toLowerCase() + "_next_rank");
	}

	/**
	 * Replace Url
	 *
	 * @param retailURL string
	 * @return retailURL string
	 */
	@Override
	public String replaceURL(final String retailURL)
	{
		return retailURL;
	}

	/**
	 * Connect
	 *
	 * @return object
	 */
	@Override
	public GlobalResponse connect(final String TOPSITE, final VDSystem.VoteType TYPE)
	{
		return (GlobalResponse) super.connect(TOPSITE, TYPE);
	}

	/**
	 * Returns response code
	 *
	 * @return _responseCode int
	 */
	public int getResponseCode()
	{
		return _responseCode;
	}

	/**
	 * Returns server votes
	 *
	 * @return _serverVotes int
	 */
	public int getServerVotes()
	{
		return _serverVotes;
	}

	/**
	 * Returns server rank
	 *
	 * @return _serverRank int
	 */
	public int getServerRank()
	{
		return _serverRank;
	}

	/**
	 * Returns server needed votes
	 *
	 * @return _serverNeededVotes int
	 */
	public int getServerNeededVotes()
	{
		return _serverNeededVotes;
	}

	/**
	 * Returns server next rank
	 *
	 * @return _serverNextRank int
	 */
	public int getServerNextRank()
	{
		return _serverNextRank;
	}

	/**
	 * Return response
	 *
	 * @param url string
	 * @return IndividualResponse object
	 */
	public static GlobalResponse OPEN(final String url)
	{
		return new GlobalResponse(url);
	}
}