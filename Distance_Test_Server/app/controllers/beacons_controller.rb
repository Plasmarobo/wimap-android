class BeaconsController < ApplicationController
  skip_before_filter :verify_authenticity_token, :only => :create
  before_action :set_beacon, only: [:show, :edit, :update, :destroy]

  # GET /beacons
  # GET /beacons.json
  def index
    @beacons = Beacon.all
  end

  # GET /beacons/1
  # GET /beacons/1.json
  # GET /beacons/1.m
  def show
    respond_to do |format|
      format.matlab {
      begin
        filename = 'beacon' + @beacon.uid.gsub(':','_') + ".m"
        File.open(Dir.pwd + "/matlab/" + filename, "w+") do |f|
          f.write("function data = " + filename[0..-3] + "()\n")
          f.write("data = [\n")
          block = Array.new(31)
          $i = 0
          begin
            block[$i] = @beacon.distance_samples.where(distance: $i).collect {|sample| sample.power.to_s}.join(',')
            $i = $i + 1
          end until $i > 30
          block = block.join(";\n")
          f.write(block)
          f.write("\n];\n")
          f.write("\nend");

        end
        send_file Dir.pwd + "/matlab/" + filename, {filename: filename, type: 'text/matlab'}
      end }
      format.html 
      format.json { render :json => @beacon }

    end
  end

  # GET /beacons/new
  def new
    @beacon = Beacon.new
  end

  # GET /beacons/1/edit
  def edit
  end

  # POST /beacons
  # POST /beacons.json
  def create
    beacon = JSON.parse(params["beacon"])
    @beacon = Beacon.new(beacon)

    respond_to do |format|
      if @beacon.save
        format.html { redirect_to @beacon, notice: 'Beacon was successfully created.' }
        format.json { render action: 'show', status: :created, location: @beacon }
      else
        format.html { render action: 'new' }
        format.json { render json: @beacon.errors, status: :unprocessable_entity }
      end
    end
  end

  # PATCH/PUT /beacons/1
  # PATCH/PUT /beacons/1.json
  def update
    respond_to do |format|
      if @beacon.update(beacon_params)
        format.html { redirect_to @beacon, notice: 'Beacon was successfully updated.' }
        format.json { head :no_content }
      else
        format.html { render action: 'edit' }
        format.json { render json: @beacon.errors, status: :unprocessable_entity }
      end
    end
  end

  # DELETE /beacons/1
  # DELETE /beacons/1.json
  def destroy
    @beacon.destroy
    respond_to do |format|
      format.html { redirect_to beacons_url }
      format.json { head :no_content }
    end
  end

  private
    # Use callbacks to share common setup or constraints between actions.
    def set_beacon
      @beacon = Beacon.find(params[:id])
    end

    # Never trust parameters from the scary internet, only allow the white list through.
    def beacon_params
      params.require(:beacon).permit(:name, :uid)
    end
end
