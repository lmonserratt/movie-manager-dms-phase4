#!/bin/bash
# Fix imports and package headers for DMS project

fix_file() {
  local file="$1"
  local header="$2"
  echo "🔧 Fixing $file ..."
  tmpfile=$(mktemp)
  echo -e "$header" > "$tmpfile"
  awk 'NR>1 {print}' "$file" >> "$tmpfile"
  mv "$tmpfile" "$file"
}

fix_file src/main/java/dms/dao/MovieDao.java "package dms.dao;

import dms.model.Movie;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface MovieDao {
    List<Movie> findAll() throws SQLException;
    Optional<Movie> findById(int id) throws SQLException;
    Movie insert(Movie movie) throws SQLException;
    boolean update(Movie movie) throws SQLException;
    boolean delete(int id) throws SQLException;
}
"

fix_file src/main/java/dms/dao/MysqlMovieDao.java "package dms.dao;

import dms.model.Movie;
import java.sql.*;
import java.util.*;
"

fix_file src/main/java/dms/service/MovieService.java "package dms.service;

import dms.dao.MovieDao;
import dms.model.Movie;
import java.sql.*;
import java.util.*;
"

fix_file src/main/java/dms/gui/MovieFormDialog.java "package dms.gui;

import dms.service.MovieService;
import dms.model.Movie;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
"

fix_file src/main/java/dms/gui/MovieTableFrameMysql.java "package dms.gui;

import dms.dao.MovieDao;
import dms.dao.MysqlMovieDao;
import dms.service.MovieService;
import dms.model.Movie;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.util.*;
"

fix_file src/main/java/dms/app/GuiMainMysql.java "package dms.app;

import dms.gui.MovieTableFrameMysql;
"

echo "✅ Done! Headers fixed."
