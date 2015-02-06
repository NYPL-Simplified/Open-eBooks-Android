package org.nypl.simplified.opds.core;

import java.net.URI;

import com.io7m.jfunctional.OptionType;

/**
 * The type of mutable builders for {@link OPDSAcquisitionFeedEntry} values.
 */

public interface OPDSAcquisitionFeedEntryBuilderType
{
  void addAcquisition(
    OPDSAcquisition a);

  void addAuthor(
    final String name);

  OPDSAcquisitionFeedEntry build();

  void setCoverOption(
    OptionType<URI> uri);

  void setSubtitleOption(
    OptionType<String> text);

  void setSummaryOption(
    OptionType<String> text);

  void setThumbnailOption(
    OptionType<URI> uri);
}