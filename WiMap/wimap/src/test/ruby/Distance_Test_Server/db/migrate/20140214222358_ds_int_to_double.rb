class DsIntToDouble < ActiveRecord::Migration
  def change
  	change_column :distance_samples, :power, :real
  	change_column :distance_samples, :distance, :real
  end
end
