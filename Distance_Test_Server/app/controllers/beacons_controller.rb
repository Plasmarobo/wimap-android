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
  def show
    filename = @beacon.uid.!gsub(':','_') + ".m"
    File.open(filename, "w+") do |f|
      f.write("function data = " + filename.substr(-2) + "()\n")
      f.write("data = [\n")
      data_rows = [];
      @beacon.distance_samples.each do |sample|
        data_rows[sample.distance] += sample.power
        data_rows[sample.distance] += ","
      end
      block = ""
      data_rows.each do |row|
        block += row[-1] + ";\n"
      end
      f.write(block[-2] + "\n")
      f.write("];\n")
      f.write("\nend");
    end
    render :file => filename, :content_type => "text/matlab"
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
